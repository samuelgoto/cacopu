package com.cacopu.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cacopu.server.MailServlet.Chat;
import com.google.inject.Singleton;

class TaskServlet implements PostHandler {
  private static final Logger logger = Logger.getLogger(
      TaskServlet.class.getName());
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String from = req.getParameter("from");
    String subject = req.getParameter("subject");

    new Chat().send(from, "\"" + subject + "\" -- ping!");
  }
}
