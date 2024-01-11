package com.centum.atm.dao;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.centum.atm.entity.Account;
import com.centum.atm.entity.AtmCard;
import com.centum.atm.entity.Transaction;

public class AtmCardDaoImpl implements IAtmCardDao 
{
  private Connection conn;
  private String sql;
  private PreparedStatement pst;
  private ResultSet rs;
  
  public AtmCardDaoImpl() throws ClassNotFoundException,SQLException
  {
	  conn=MyConnection.getConnection();
  }
  private static Map<String, Integer> failedLoginAttempts = new HashMap<>();
  public String validateLoginDetails(AtmCard atmcard) throws SQLException {
	    String cardNo = atmcard.getCardNo();
	    String cardStatusSql = "SELECT cardStatus, customerId FROM atmcard WHERE cardNo = ?";
	    
	    pst = conn.prepareStatement(cardStatusSql);
	    pst.setString(1, cardNo);
	    ResultSet statusResult = pst.executeQuery();
	    String cardStatus = null;
	    int customerId = -1; // Initialize customerId to -1

	    if (statusResult.next()) {
	        cardStatus = statusResult.getString("cardStatus");
	        customerId = statusResult.getInt("customerId");
	    }

	    // Check if the card exists
	    sql = "SELECT * FROM atmcard WHERE cardNo=?";
	    pst = conn.prepareStatement(sql);
	    pst.setString(1, cardNo);
	    ResultSet rs = pst.executeQuery();

	    if (rs.next()) {
	        String storedPin = rs.getString("pinNo");
	        String enteredPin = atmcard.getPinNo();

	        if ("blocked".equals(cardStatus)) {
	            // If the card is blocked, deny access regardless of the PIN
	            return "cardblock.html";
	        } else if (storedPin.equals(enteredPin)) {
	            // Successful login
	            failedLoginAttempts.put(cardNo, 0); // Reset failed attempts

	           
	         // Assuming you have retrieved the customer's name as 'name'
	            String name = getName(customerId);
	            String redirectUrl = "AtmOperations1.html?name=" + name;
	            return redirectUrl;	           
	        } else {
	            // Failed login
	            int attempts = failedLoginAttempts.getOrDefault(cardNo, 0);
	            attempts++;

	            if (attempts >= 3) {
	                // If failed attempts reach 3 or more, block the card
	                updateCardStatus(cardNo, "blocked");
	                failedLoginAttempts.remove(cardNo); // Remove from tracking
	                return "CardBlocked.html";
	            } else {
	                failedLoginAttempts.put(cardNo, attempts); // Update failed attempts
	                return "index.html";
	            }
	        }
	    } else {
	        return "index.html"; // Handle card not found
	    }
	}

	private String getName(int customerId) throws SQLException {
		    String name = null;
		    String sql = "SELECT name FROM customer WHERE customerId=?";
		    PreparedStatement preparedStatement = conn.prepareStatement(sql);
		    preparedStatement.setInt(1, customerId);
		    ResultSet resultSet = preparedStatement.executeQuery();

		    if (resultSet.next()) {
		        name = resultSet.getString("name");
		    }
		    return name;
		}

  private void updateCardStatus(String cardNo, String cardStatus) throws SQLException {
	    sql = "UPDATE atmcard SET cardStatus=? WHERE cardNo=?";
	    pst = conn.prepareStatement(sql);
	    pst.setString(1, cardStatus);
	    pst.setString(2, cardNo);
	    pst.executeUpdate();
	}


  public String pinGenerationUser(AtmCard atmcard) throws SQLException {
	    // Validate account number, card number, and expiry date
	    sql = "SELECT cvvNo, pinNo FROM atmcard WHERE accountno = ? AND cardNo = ? AND expiryDate = ?";
	    pst = conn.prepareStatement(sql);
	    pst.setString(1, atmcard.getAccountNo());
	    pst.setString(2, atmcard.getCardNo());
	    pst.setString(3, atmcard.getExpiryDate());
	    ResultSet cardDataResult = pst.executeQuery();
	    if (cardDataResult.next()) {
	        String storedCVV = cardDataResult.getString("cvvNo");
	        String storedPIN = cardDataResult.getString("pinNo");
	        if (storedCVV == null && storedPIN == null) {
	            // The CVV and PIN in the database are null, which means it's a new card.
	            // Check if the provided CVV and PIN are not null before updating the database
	            if (atmcard.getCvvNo() != null && atmcard.getPinNo() != null) {
	                // Update the CVV and PIN in the database
	                String updateQuery = "UPDATE atmcard SET cvvNo = ?, pinNo = ?,cardStatus = 'active' WHERE accountno = ? AND cardNo = ?";
	                pst = conn.prepareStatement(updateQuery);
	                pst.setString(1, atmcard.getCvvNo());
	                pst.setString(2, atmcard.getPinNo());
	                pst.setString(3, atmcard.getAccountNo());
	                pst.setString(4, atmcard.getCardNo());
	                int rowsAffected = pst.executeUpdate();
	                if (rowsAffected > 0) {
	                    // Redirect the user to index.html
	                    return "index.html";
	                } else {
	                    return "Failed to update CVV and PIN";
	                }
	            } else {
	                return "CVV and PIN cannot be null for a new card";
	            }
	        } else {
	            return "CVV and PIN already exist for this card";
	        }
	    } else {
	    	return "Invalid.html";	        
	    }
	}

  
  public String deposit(Transaction transaction, String cardNo) throws SQLException { 

	        String insertTransactionSql = "INSERT INTO transaction (amount, cardNo, transactionType) VALUES (?, ?, ?)";
	        pst = conn.prepareStatement(insertTransactionSql);	      
	        pst.setBigDecimal(1, transaction.getAmount());
	        pst.setString(2, cardNo);
	        pst.setString(3, "CR");      
	        int no = pst.executeUpdate();
	        String retrieveAccountNoSql = "SELECT accountno FROM atmcard WHERE cardNo = ?";
	        pst = conn.prepareStatement(retrieveAccountNoSql);
	        pst.setString(1, cardNo);
	        ResultSet resultSet = pst.executeQuery();
	        String accountNo = null;
	        if (resultSet.next()) {
	            accountNo = resultSet.getString("accountNo");
	        } else {
	            // Handle the case where the card number is not found in the AtmCard table
	            return "Card number not found.";
	         
	        }
	        String updateBalanceSql = "UPDATE Account SET balance = balance + ? WHERE accountno = ?";
	        pst = conn.prepareStatement(updateBalanceSql);

	        // Set the values for the prepared statement
	        pst.setBigDecimal(1, transaction.getAmount());
	        pst.setString(2, accountNo);

	        int rowsUpdated = pst.executeUpdate();

	        if (rowsUpdated > 0) {
	            return "Thanks.html";
	        } else {
	            // Handle the case where no rows were updated (e.g., account not found)
	            return "Account not found or balance update failed.";
	        }
	    }
	

  public String withdraw(Transaction transaction, String cardNo) throws SQLException {
	    sql = "INSERT INTO transaction (amount, cardNo, transactionType) VALUES (?, ?, ?)";
	    pst = conn.prepareStatement(sql);

	    // Set the values for the prepared statement
	    pst.setBigDecimal(1, transaction.getAmount()); // Assuming transaction.getAmount() returns a BigDecimal
	    pst.setString(2, cardNo);
	    pst.setString(3, "DR"); // Assuming this is a withdrawal transaction
	    int no = pst.executeUpdate();
	    String retrieveAccountNoSql = "SELECT accountno FROM atmcard WHERE cardNo = ?";
	    pst = conn.prepareStatement(retrieveAccountNoSql);
	    pst.setString(1, cardNo);
	    ResultSet resultSet = pst.executeQuery();     
	    String accountNo = null;
	    if (resultSet.next()) {
	        accountNo = resultSet.getString("accountNo");	       
	    } else {
	        // Handle the case where the card number is not found in the AtmCard table
	        return "Card number not found.";
	    }
	    String retrieveBalanceNoSql = "SELECT balance FROM account WHERE accountNo = ?";
	    pst = conn.prepareStatement(retrieveBalanceNoSql);
	    pst.setString(1, accountNo);
	    ResultSet rs = pst.executeQuery();     
	    BigDecimal balance = null;
	    if(rs.next()) {
	    	balance = rs.getBigDecimal("balance");	    
          // Check if the balance is sufficient for the withdrawal
	    if (balance.compareTo(transaction.getAmount()) >= 0) {
	        String updateBalanceSql = "UPDATE Account SET balance = balance - ? WHERE accountno = ?";
	        pst = conn.prepareStatement(updateBalanceSql);

	        // Set the values for the prepared statement
	        pst.setBigDecimal(1, transaction.getAmount());
	        pst.setString(2, accountNo);
	        int rowsUpdated = pst.executeUpdate();

	        if (rowsUpdated > 0) {
	            return "Thanksw.html"; // Withdrawal successful
	        } else {
	            // Handle the case where no rows were updated (e.g., account not found)
	            return "Account not found or balance update failed.";
	        }
	    } else {
	        return "Insufficient.html"; // Return a message indicating insufficient funds
	    }
	    }
	    else {
	    	return retrieveBalanceNoSql;
	    }
		
	}

  public BigDecimal checkBalance(String cardNo) throws SQLException {
	    String retrieveAccountNoSql = "SELECT accountno FROM atmcard WHERE cardNo = ?";
	    pst = conn.prepareStatement(retrieveAccountNoSql);
	    pst.setString(1, cardNo);
	    ResultSet resultSet = pst.executeQuery();

	    String accountNo = null;

	    if (resultSet.next()) {
	        accountNo = resultSet.getString("accountNo");
	    } else {
	        // Handle the case where the card number is not found in the AtmCard table
	        throw new SQLException("Card number not found.");
	    }

	    String updateBalanceSql = "SELECT balance FROM account WHERE accountNo=?";
	    pst = conn.prepareStatement(updateBalanceSql);
	    pst.setString(1, accountNo);

	    ResultSet rs = pst.executeQuery();

	    if (rs.next()) {
	        BigDecimal balanceAmount = rs.getBigDecimal("balance");
	        return balanceAmount; // Return the balance
	    } else {
	        // Handle the case where no rows were updated (e.g., account not found)
	        throw new SQLException("Account not found or balance retrieval failed.");
	    }
  }
  
  public List<Transaction> miniStatement( String cardNo) throws SQLException{
	  List<Transaction> list = new ArrayList<Transaction>();
	    sql = "SELECT * FROM transaction WHERE cardNo= ?";
	    
	    pst = conn.prepareStatement(sql);
	        pst.setString(1, cardNo);
	        ResultSet rs = pst.executeQuery();
	        
	        while (rs.next()) 
	        {
	            Transaction transaction = new Transaction();
	            transaction.setTransactionId(rs.getInt("transactionId"));
	            transaction.setCardNo(rs.getString("cardNo"));
	            transaction.setTransactionType(rs.getString("transactionType"));
	            transaction.setAmount(rs.getBigDecimal("amount"));
	            transaction.setTransactionDate(rs.getString("transactionDate"));
	            list.add(transaction);
	        }
	        return list;	 
  }
    public boolean updatePin(String cardNo,String pinNo, String newPin)throws SQLException{
    	
    	
	    String updateQuery = "UPDATE atmcard SET pinNo = ? WHERE cardNo = ? AND pinNo = ?";
      
      // Prepare the SQL statement
      PreparedStatement pst = conn.prepareStatement(updateQuery);
      pst.setString(1, newPin);
      pst.setString(2, cardNo);
      pst.setString(3, pinNo);

      // Execute the update
      int rowsAffected = pst.executeUpdate();

      return rowsAffected > 0; // Returns true if the PIN was successfully updated
      }   
  }

