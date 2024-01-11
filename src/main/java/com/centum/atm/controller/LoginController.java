package com.centum.atm.controller;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.centum.atm.dao.AtmCardDaoImpl;
import com.centum.atm.entity.AtmCard;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        String cardNo = req.getParameter("cardNo");
        String pinNo = req.getParameter("pinNo");
        System.out.println(cardNo);
        System.out.println(pinNo);
        try {
            AtmCard atmcard = new AtmCard();
            AtmCardDaoImpl atmcardDao = new AtmCardDaoImpl();
            atmcard.setCardNo(cardNo);
            atmcard.setPinNo(pinNo);
            ServletContext sc = req.getServletContext();
            sc.setAttribute("cardNo", cardNo);
            ServletContext sc2 = req.getServletContext();
            sc2.setAttribute("pinNo", pinNo);
            
           String result = atmcardDao.validateLoginDetails(atmcard);

            if ("Card is blocked!!! Contact the bank".equals(result)) {
                // The card is blocked, set an attribute to indicate this
                req.setAttribute("cardBlocked", true);
                req.getRequestDispatcher("login.html").forward(req, res);
            } else {
                // For successful logins or other messages, forward accordingly
                req.getRequestDispatcher(result).forward(req, res);
            }


            //req.getRequestDispatcher(atmcardDao.validateLoginDetails(atmcard)).forward(req, res);

        
        } catch (Exception e) {
            System.out.println(e);
        }
    }	
}
