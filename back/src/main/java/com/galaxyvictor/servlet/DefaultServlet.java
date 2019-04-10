package com.galaxyvictor.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {
    "/login",
    "/register",
    "/research",
    "/colonies",
    "/galaxy",
    "/civilizations",
    "/fleets",
    "/planets",
    "/commerce"
})
public class DefaultServlet extends HttpServlet {

    private static final long serialVersionUID = -5294205953870972749L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("Utf-8");
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/");
        dispatcher.forward(request, response);
    }
    
}