<%-- 
    Document   : AuthJSP
    Created on : Mar 25, 2024, 1:33:41 AM
    Author     : Network2 Group
--%>

<%@page import="najah.network.IDatabase"%>
<%@page contentType="text/plain" pageEncoding="UTF-8"%>
<%
    ServletContext ctx = request.getServletContext();
    IDatabase database = (IDatabase) ctx.getAttribute("db");

    if (database == null) {
        out.print("Error: Database not found.");
        return;
    }

    String idParam = request.getParameter("id");
    String password = request.getParameter("password");
    int id = 0;

    if (idParam != null && !idParam.isEmpty() && password != null && !password.isEmpty()) {
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            out.print("NO"); // Invalid ID format
            return;
        }

        boolean isValidUser = database.isValidUserById(id, password);
        out.print(isValidUser ? "OK" : "NO");
    } else {
        out.print("NO"); // Missing or empty ID/password
    }
%>
