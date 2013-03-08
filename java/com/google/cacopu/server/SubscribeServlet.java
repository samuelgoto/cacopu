package com.cacopu.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cacopu.server.MailServlet.Chat;
import com.cacopu.server.ServerModule.Jid;
import com.google.appengine.api.xmpp.Subscription;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

class SubscribeServlet implements PostHandler {
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    XMPPService xmpp = XMPPServiceFactory.getXMPPService();

    Subscription subscription = xmpp.parseSubscription(req);
    
    new Chat().send(subscription.getFromJid().getId(),
        "Yo! How is it going? I'm X and I'm here to help :) You can try thing like 'Remind me that my SSN is 1234.' or 'What's 2+2?'.");
  }
}
