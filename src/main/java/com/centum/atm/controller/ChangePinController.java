package com.centum.atm.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.centum.atm.dao.AtmCardDaoImpl;
import com.centum.atm.entity.AtmCard;
@WebServlet("/changepin")
public class ChangePinController extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
 
        String pinNo = (String) req.getServletContext().getAttribute("pinNo");
        String cardNo=(String)req.getServletContext().getAttribute("cardNo");// Retrieve the PIN from the session
        String currentPin = req.getParameter("cPinNo");
        String newPin = req.getParameter("pinNo");
        String confirmNewPin = req.getParameter("cpin");
        
        String pin = (newPin != null) ? newPin.trim() : null;
	    String confirmpin = (confirmNewPin != null) ? confirmNewPin.trim() : null;
	    System.out.println("currentPin from session: " + pinNo);
	    System.out.println("currentPin entered by user: " + currentPin);

        
        if (pinNo != null && currentPin != null && pinNo.equals(currentPin)) {
            if (pin != null && confirmpin != null && pin.equals(confirmpin)) {
                try {
                    // Create an instance of the ATM card
                    AtmCard atmCard = new AtmCard();
                    atmCard.setPinNo(newPin); // Set the new PIN

                    // Create an instance of your DAO and update the PIN
                    AtmCardDaoImpl atmCardDao = new AtmCardDaoImpl();
                    boolean pinUpdated = atmCardDao.updatePin(cardNo,pinNo, newPin);

                    if (pinUpdated) {
                        // PIN successfully updated
                        out.println("<h1 style='color:green'>PIN Successfully Changed</h1>");
                    } else {
                        // PIN update failed
                        out.println("<h1 style='color:red'>PIN Change Failed</h1>");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                out.println("<h1 style='color:red'>New PIN and Confirm PIN do not match. Please re-enter.</h1>");
            }
        } else {
            out.println("<h1 style='color:red'>Current PIN is incorrect.</h1>");
        }
    }
}