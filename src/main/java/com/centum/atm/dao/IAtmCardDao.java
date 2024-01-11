package com.centum.atm.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.centum.atm.entity.AtmCard;
import com.centum.atm.entity.Transaction;


public interface IAtmCardDao {
	String validateLoginDetails(AtmCard atmcard)throws SQLException;
	String pinGenerationUser(AtmCard atmcard) throws SQLException;
	String deposit(Transaction transaction, String cardNo) throws SQLException;	
	String withdraw(Transaction transaction, String cardNo) throws SQLException;
	BigDecimal checkBalance(String cardNo) throws SQLException;
	List<Transaction> miniStatement( String cardNo) throws SQLException;
	boolean updatePin(String cardNo,String pinNo, String newPin)throws SQLException;
}