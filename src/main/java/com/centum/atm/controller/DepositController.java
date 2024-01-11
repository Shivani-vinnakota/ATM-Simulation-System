package com.centum.atm.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centum.atm.dao.AtmCardDaoImpl;
import com.centum.atm.entity.Transaction;

@WebServlet("/deposit")
public class DepositController extends HttpServlet 
{ 
public void doPost(HttpServletRequest req ,HttpServletResponse res )throws ServletException,IOException
  {
	  res.setContentType("text/html");
	  PrintWriter out=res.getWriter();

	  String cardNo=(String)req.getServletContext().getAttribute("cardNo");
	  String amountStr = req.getParameter("amount");
	 
	  try
	  {
		 Transaction transaction = new Transaction(); 
			 AtmCardDaoImpl atmcardDao = new AtmCardDaoImpl();	 
		
			 BigDecimal amount = new BigDecimal(amountStr);
			 transaction.setAmount(amount);
			 String resultPage = atmcardDao.deposit(transaction, cardNo);

	            if ("deposit_1.html".equals(resultPage)) {	                
	            	req.setAttribute("successMessage", "Deposit of $" + amount+ " was successful.");
	                req.getRequestDispatcher("Thanks.html").forward(req, res);
	            } else {
	                // Handle other cases (e.g., error page)
	                req.getRequestDispatcher(resultPage).forward(req, res);
	            }
			 
	  }
	  catch(Exception e)
	  {
		  System.out.println(e); 
	  }
	  
	  
  }
}


