package com.cacopu.server;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

class CheckoutServlet implements GetHandler, PostHandler {
  private final ProductBuilder productBuilder;
  private final Renderer renderer;

  @Inject
  CheckoutServlet(
      ProductBuilder productBuilder,
      Renderer renderer) {
    this.productBuilder = productBuilder;
    this.renderer = renderer;
  }

  static class MailSender {
    public void send(String email, String name, String subject, String message, String messageHtml) throws UnsupportedEncodingException, MessagingException {
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(
          "samuelgoto@gmail.com", "Evolving.biz"));
      msg.addRecipient(Message.RecipientType.TO,
          new InternetAddress(email, name));
      msg.setSubject(subject);
      msg.setText(message);
      Multipart mp = new MimeMultipart(); 
      MimeBodyPart htmlPart = new MimeBodyPart(); 
      htmlPart.setContent(messageHtml, "text/html"); 
      mp.addBodyPart(htmlPart); 
      msg.setContent(mp);
      Transport.send(msg);
    }
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    boolean confirmed = !Strings.isNullOrEmpty(req.getParameter("confirm"));

    if (Strings.isNullOrEmpty(id)) {
      resp.sendRedirect("/");
      return;
    }

    try {
      SoyMapData data = new SoyMapData();
      data.put("product", productBuilder.build(Long.parseLong(id)));
      data.put("buyer", new SoyMapData(
          "name", req.getParameter("buyer.name"),
          "email", req.getParameter("buyer.email"),
          "phone", req.getParameter("buyer.phone"),
          "zip", req.getParameter("buyer.zip")
      ));

      if (confirmed) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity result = datastore.get(
            KeyFactory.createKey("Employee", Long.valueOf(id)));
        
        User user = (User) result.getProperty("user");
        new MailSender().send(
            user.getEmail(), user.getNickname(),
            "new order placed!",
            renderer.render("cacopu.emailText", data),
            renderer.render("cacopu.emailHtml", data)
            );
      }

      resp.getWriter().print(
          renderer.render(!confirmed ? "cacopu.reviewHtml" : "cacopu.confirmedHtml", data));
    } catch (EntityNotFoundException e) {
      resp.sendRedirect("/");
      return;
    } catch (MessagingException e) {
      // failed to send email.
      // TODO(goto): create a message to the user here.
      resp.sendRedirect("/");
      return;
    }
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");

    if (id == null) {
      resp.sendRedirect("/");
      return;
    }

    try {
      SoyMapData product = productBuilder.build(Long.parseLong(id)); 
      resp.getWriter().print(renderer.render("cacopu.checkoutHtml",
          new SoyMapData("product", product)));
    } catch (EntityNotFoundException e) {
      resp.sendRedirect("/");
      return;
    }
  }

  static class Renderer {
    String render(String template, SoyMapData data) {
      // Bundle the Soy files for your project into a SoyFileSet.
      SoyFileSet sfs = new SoyFileSet.Builder().add(
          new File("templates/index.soy")).build();

      // Compile the template into a SoyTofu object.
      // SoyTofu's newRenderer method returns an object that can render any template in file set.
      SoyTofu tofu = sfs.compileToJavaObj();

      return tofu.newRenderer(template)
      .setData(data) 
      .render();
    }
  }

  static class ProductBuilder {
    SoyMapData build(long id) throws EntityNotFoundException {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity result = datastore.get(
          KeyFactory.createKey("Employee", id));
      return new SoyMapData(
          "id", Long.toString(id),
          "name", result.getProperty("name"),
          "price", result.getProperty("price"),
          "description", result.getProperty("description")
      );
    }
  }
}
