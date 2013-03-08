package com.cacopu.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Strings;
import com.google.inject.Singleton;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

public class IndexServlet implements GetHandler {
  @Override public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    // Bundle the Soy files for your project into a SoyFileSet.
    SoyFileSet sfs = new SoyFileSet.Builder().add(
        new File("templates/index.soy")).build();

    // Compile the template into a SoyTofu object.
    // SoyTofu's newRenderer method returns an object that can render any template in file set.
    SoyTofu tofu = sfs.compileToJavaObj();

    Integer id = parseId(req);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    if (id == null) {
      if (user != null) {
        resp.sendRedirect("/home");
        return;
      }

      // Call the template with no data.
      resp.getWriter().print(
          tofu.newRenderer("cacopu.indexHtml")
          .render());
      return;
    }
    
    try {
      Entity result = datastore.get(
          KeyFactory.createKey("Employee", id));
      
      URI uri = new URI(req.getRequestURI());
      
      User owner = (User) result.getProperty("user");
      // TODO(goto): add tests and ensure this is suficient.
      if (!Strings.isNullOrEmpty(req.getParameter("delete")) &&
          user != null &&
          user.getEmail().equals(owner.getEmail())) {
        datastore.delete(KeyFactory.createKey("Employee", id));
        resp.sendRedirect("/home");
        return;
      }
      
      if (!uri.getPath().substring(1).contains("/")) {
        String name = result.getProperty("name").toString();
        name = name.replace(' ', '-');
        name = URLEncoder.encode(name);
        resp.sendRedirect(result.getKey().getId() + "/" + name);
        return;
      }
      
      resp.getWriter().print(
          tofu.newRenderer("cacopu.productHtml")
          .setData(new SoyMapData(
              "loggedIn", user != null,
              "name", result.getProperty("name"),
              "price", result.getProperty("price"),
              "url", req.getRequestURI(),
              "id", id,
              "description", result.getProperty("description"),
              "logoutUrl", userService.createLogoutURL(req.getRequestURI()),
              "loginUrl", userService.createLoginURL(req.getRequestURI())
              ))
          .render());
    } catch (EntityNotFoundException e) {
      resp.getWriter().print(
          tofu.newRenderer("cacopu.productNotFoundHtml")
          .render());
    } catch (URISyntaxException e) {
      resp.sendRedirect("/");
    }
  }

  Integer parseId(HttpServletRequest req) {
    try {
      String path = new URI(req.getRequestURI()).getPath();
      path = path.substring(1);
      if (path.contains("/")) {
        path = path.substring(0, path.indexOf('/'));
      }
      return Integer.parseInt(path);
    } catch (URISyntaxException e) {
      return null;
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
