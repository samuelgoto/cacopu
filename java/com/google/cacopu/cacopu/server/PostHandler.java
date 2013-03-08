package com.cacopu.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PostHandler {
  void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
