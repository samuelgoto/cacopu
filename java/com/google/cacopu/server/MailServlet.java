package com.cacopu.server;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ibm.icu.util.Calendar;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

@SuppressWarnings("serial")
public class MailServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(
      MailServlet.class.getName());
  interface Email {
    String from();
    List<String> to();
    String subject();
    String body();
  }

  private class EmailBuilder {
    private String from = "";
    private List<String> to = Lists.newArrayList();
    private String subject = "";
    private String body = "";

    EmailBuilder setFrom(String from) {
      this.from = from;
      return this;
    }

    EmailBuilder addTo(String to) {
      this.to.add(to);
      return this;
    }

    EmailBuilder setSubject(String subject) {
      this.subject = subject;
      return this;
    }

    EmailBuilder setBody(String body) {
      this.body = body;
      return this;
    }

    Email build() {
      return new Email() {
        @Override
        public String body() {
          return body;
        }

        @Override
        public String from() {
          return from;
        }

        @Override
        public String subject() {
          return subject;
        }

        @Override
        public List<String> to() {
          return to;
        }
      };
    }
  }

  Email parseEmail(MimeMessage message) throws MessagingException, IOException {
    logger.info("parsing email: " + message.toString());
    
    EmailBuilder builder = new EmailBuilder();
    builder.setSubject(message.getSubject());
    String contentType = message.getContentType();
    Address[] recipients = message.getAllRecipients();
    for (Address recipient : recipients) {
      builder.addTo(recipient.toString());
    }
    InternetAddress[] from = (InternetAddress[]) message.getFrom();
    Preconditions.checkArgument(from.length == 1);
    builder.setFrom(from[0].getAddress());
    String content = "";
    if (contentType.startsWith("text/plain")) {
      content = (String) message.getContent();
    } else if (contentType.startsWith("multipart/alternative")) {
      MimeMultipart multiPart = (MimeMultipart) message.getContent();
      for (int i = 0; i < multiPart.getCount(); i++) {
        BodyPart part = multiPart.getBodyPart(i);
        if ("text/plain; charset=UTF-8".equals(part.getContentType()) ||
            "text/plain; charset=ISO-8859-1".equals(part.getContentType())) {
          content += (String) part.getContent();
        } else if ("text/html; charset=UTF-8".equals(part.getContentType()) ||
            "text/html; charset=ISO-8859-1".equals(part.getContentType())) {
          logger.warning("non handled body part type: " + part.getContentType());
        } else {
          throw new IllegalArgumentException("unknown body part type: " + part.getContentType());
        }
      }
    } else {
      throw new IllegalArgumentException("unknown email content type: " + contentType);
    }
   
    builder.setBody(content);
    return builder.build();
  }
  
  static class Chat {
    public void send(String email, String message) {
      logger.info("sending a XMPP message to " + email);
      JID jid = new JID(email);

      com.google.appengine.api.xmpp.Message msg = new MessageBuilder()
          .withRecipientJids(jid)
          .withBody(message)
          .build();

      XMPPService xmpp = XMPPServiceFactory.getXMPPService();
      SendResponse status = xmpp.sendMessage(msg);
      boolean success = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);
      // TODO(goto): throw exception when !success.
      if (!success) {
        logger.severe("failed to send XMPP");
      }
    }
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    try {
      logger.info("received an email");
      MimeMessage message = new MimeMessage(session, req.getInputStream());
      // NOTE(goto): we need to find a way to determine the user TimeZone.
      Email email = parseEmail(message);
      logger.info("parsed email from " + email.from());
      logger.warning("parsed email body:\n" + email.body());
      message.reply(true);
      Address[] sender = new Address[] {new InternetAddress(email.from(), "")};
      message.setRecipients(Message.RecipientType.TO,
          sender);
      
      GregorianCalendar calendar = new GregorianCalendar(2012, Calendar.APRIL, 15);
      calendar.setTimeZone(TimeZone.getTimeZone("PST"));
      
      long now = System.currentTimeMillis();
      long eta = now + TimeUnit.MINUTES.toMillis(1);
      
      long duration = eta - now;

      String reminder = "";
      if (TimeUnit.MILLISECONDS.toDays(duration) != 0) {
        // TODO(goto): break this into years and months.
        reminder += TimeUnit.MILLISECONDS.toDays(duration) + " day(s)";
      } else if (TimeUnit.MILLISECONDS.toHours(duration) != 0) {
          reminder += TimeUnit.MILLISECONDS.toHours(duration) + " hour(s)";
      } else if (TimeUnit.MILLISECONDS.toMinutes(duration) != 0) {
        reminder += TimeUnit.MILLISECONDS.toMinutes(duration) + " minute(s)";
      } else if (TimeUnit.MILLISECONDS.toSeconds(duration) != 0) {
        reminder += TimeUnit.MILLISECONDS.toSeconds(duration) + " second(s)";
      }
      
      new Chat().send(email.from(), "\"" + email.subject() + "\" -- I'm on it! I'll ping you in " + reminder + ".");
      
      Queue queue = QueueFactory.getDefaultQueue();
      queue.add(
          withUrl("/task")
          .etaMillis(eta)
          .param("from", email.from())
          .param("subject", email.subject())
          );
   } catch (MessagingException e) {
      throw new IOException("Invalid email", e);
    }
  }
}