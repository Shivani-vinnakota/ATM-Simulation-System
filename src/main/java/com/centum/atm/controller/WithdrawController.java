package com.centum.atm.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centum.atm.dao.AtmCardDaoImpl;
import com.centum.atm.entity.Transaction;

@WebServlet("/withdraw")
public class WithdrawController extends HttpServlet{
	public void doPost(HttpServletRequest req ,HttpServletResponse res )throws ServletException,IOException,ClassCastException
	  {
	res.setContentType("text/html");
	  PrintWriter out=res.getWriter();

	  String cardNo=(String)req.getServletContext().getAttribute("cardNo");		
	 // String amountStr1 = req.getParameter("amount");
	  String amountStr1 =req.getParameter("amount");
	 
	  try
	  {
		 Transaction transaction = new Transaction(); 
			 AtmCardDaoImpl atmcardDao = new AtmCardDaoImpl();	 
		
			BigDecimal amount = new BigDecimal(amountStr1);
			 transaction.setAmount(amount);
			 String resultPage = atmcardDao.withdraw(transaction, cardNo);

	            if ("withdraw.html".equals(resultPage)) {	                	            	
	                req.getRequestDispatcher("Thanksw.html").forward(req, res);
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
