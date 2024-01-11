package com.centum.atm.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centum.atm.dao.AtmCardDaoImpl;
import com.centum.atm.entity.Account;
import com.centum.atm.entity.Transaction;

@WebServlet("/checkbalance")
public class CheckBalanceController extends HttpServlet 
{ 
public void doGet(HttpServletRequest req ,HttpServletResponse res )throws ServletException,IOException
  {
	  res.setContentType("text/html");
	  PrintWriter out=res.getWriter();

	  String cardNo=(String)req.getServletContext().getAttribute("cardNo");
	
	 
	  try
	  {
			 AtmCardDaoImpl atmcardDao = new AtmCardDaoImpl();	 			
			 BigDecimal balanceAmount = atmcardDao.checkBalance(cardNo);
			 Account account = new Account();
			 
			 if (balanceAmount != null) {
		            res.getWriter().println("Available Balance is : " + balanceAmount);
		        } else {
		            res.getWriter().println("Zero Balance");
		        }
			
	           /* if ("checkbalance.html".equals(resultPage)) {	
	       		 out.print("<h2>your Balance is : +account.getBalance()+</h2>");
	            	//req.setAttribute("successMessage", "Deposit of $" + amount+ " was successful.");
	          
	            } else {
	                // Handle other cases (e.g., error page)
	              //  req.getRequestDispatcher(resultPage).forward(req, res);
	            	
	            }*/
			 
	  }
	  catch(Exception e)
	  {
		  System.out.println(e); 
	  }
	  
	  
  }
}


