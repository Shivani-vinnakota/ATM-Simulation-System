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


@WebServlet("/pingeneration")
public class PinGenerationController extends HttpServlet 
{

	
public void doPost(HttpServletRequest req ,HttpServletResponse res )throws ServletException,IOException,NumberFormatException
  {
	  res.setContentType("text/html");
	  PrintWriter out=res.getWriter();
 
	  String cardNo = req.getParameter("cardNo");
	  String pinNo = req.getParameter("pinNo");
	  String expiryDate= req.getParameter("expiryDate");
	  String cvvNo = req.getParameter("cvvNo");	  
	  String accNumber = req.getParameter("accountno");
	  String cpin = req.getParameter("cpin");
	  
	  String pin = (pinNo != null) ? pinNo.trim() : null;
	    String confirmpin = (cpin != null) ? cpin.trim() : null;
	  if (pin != null && confirmpin != null && pin.equals(cpin)) {
          try {
              AtmCard atmcard = new AtmCard();
              AtmCardDaoImpl atmcardDao = new AtmCardDaoImpl();

              atmcard.setCardNo(cardNo);
              atmcard.setPinNo(pinNo);
              atmcard.setCvvNo(cvvNo);
              atmcard.setExpiryDate(expiryDate);
              atmcard.setAccountNo(accNumber);
              System.out.println("red");
             
              
              req.getRequestDispatcher(atmcardDao.pinGenerationUser(atmcard)).forward(req, res);
              
          }
	      catch(Exception e)
	     {
		  System.out.println(e); 
	     }
	  }
	  else
	  {
		  out.println("<h3 style=color:red>Both passwords are doesnot match</h3>");
	  }
  }
}