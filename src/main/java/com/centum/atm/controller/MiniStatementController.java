package com.centum.atm.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centum.atm.dao.AtmCardDaoImpl;
import com.centum.atm.entity.Transaction;
@WebServlet("/ministatement")
public class MiniStatementController extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		res.setContentType("text/html");
		PrintWriter out =res.getWriter();
		String cardNo = (String) req.getServletContext().getAttribute("cardNo");
		
		try
		{
			AtmCardDaoImpl atmcardDao =new AtmCardDaoImpl();
			List<Transaction> list =atmcardDao.miniStatement(cardNo);
	       
				if(list.size()!=0)
					{
						out.println("<head>");
		            	out.println("<link rel='stylesheet' type='text/css' href='styles.css'>");
		            	out.println("</head>");
						out.print("<table align='center' width='70%' border='1'>");
						out.print("<caption>Mini Statement</caption>");
						out.print("<tr><th>transactionId</th><th>Card Number</th><th>Transaction Type</th><th>Amount</th><th>TransactionDate And Time </th></tr>");
						
						for (Transaction transaction : list) 
						{
							out.print("<tr>");
							out.print("<td>" + transaction.getTransactionId() + "</td>");
							out.print("<td>" + transaction.getCardNo() + "</td>");
							out.print("<td>" + transaction.getTransactionType()+ "</td>");
							out.print("<td>" + transaction.getAmount() + "</td>");
							out.print("<td>" + transaction.getTransactionDate() + "</td>");
							out.print("</tr>");
						}
						out.print("</table>");						
						out.print("<h3><a href='AtmOperations1.html'>Back</a></h3>");
					}
					else
					{
						out.print("<h3 style='color:red'>  No record Found..");
					}				
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}
