package com.cacopu.server;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Singleton;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

class CreateServlet implements GetHandler, PostHandler {
  @Override public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    Entity employee = new Entity("Employee");
    employee.setProperty("name", req.getParameter("name"));
    employee.setProperty("price", req.getParameter("price"));
    employee.setProperty("description", req.getParameter("description"));
    Date hireDate = new Date();
    employee.setProperty("creation", hireDate);
    employee.setProperty("user", user);

    datastore.put(employee);

    resp.sendRedirect("/" + employee.getKey().getId());
    // resp.sendRedirect("/home");
  }

  @Override public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    // Bundle the Soy files for your project into a SoyFileSet.
    SoyFileSet sfs = new SoyFileSet.Builder().add(
        new File("templates/index.soy")).build();

    // Compile the template into a SoyTofu object.
    // SoyTofu's newRenderer method returns an object that can render any template in file set.
    SoyTofu tofu = sfs.compileToJavaObj();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    // The Query interface assembles a query
    Query q = new Query("Employee");
    // q.addFilter("lastName", Query.FilterOperator.EQUAL, lastNameParam);
    q.addFilter("user", Query.FilterOperator.EQUAL, user);

    // PreparedQuery contains the methods for fetching query results
    // from the datastore
    PreparedQuery pq = datastore.prepare(q);

    SoyListData products = new SoyListData();
    
    for (Entity result : pq.asIterable()) {
      products.add(new SoyMapData(
          "id", Long.toString(result.getKey().getId()),
          "name", result.getProperty("name"),
          "creation", result.getProperty("creation").toString()
          ));
    }

    resp.getWriter().print(
        tofu.newRenderer("cacopu.createHtml")
        .setData(new SoyMapData(
            "products", products,
            "logoutUrl", userService.createLogoutURL("/")))
        .render());
  }
}
