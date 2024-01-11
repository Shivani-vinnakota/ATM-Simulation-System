package com.centum.atm.entity;

public class AtmCard {
private String cardNo,pinNo,cvvNo,cardStatus,expiryDate,accountNo;
private Integer customerId;
public String getCardNo() {
	return cardNo;
}
public void setCardNo(String cardNo) {
	this.cardNo = cardNo;
}
public String getPinNo() {
	return pinNo;
}
public void setPinNo(String pinNo) {
	this.pinNo = pinNo;
}
public String getCvvNo() {
	return cvvNo;
}
public void setCvvNo(String cvvNo) {
	this.cvvNo = cvvNo;
}
public String getCardStatus() {
	return cardStatus;
}
public void setCardStatus(String cardStatus) {
	this.cardStatus = cardStatus;
}
public String getExpiryDate() {
	return expiryDate;
}
public void setExpiryDate(String expiryDate) {
	this.expiryDate = expiryDate;
}
public String getAccountNo() {
	return accountNo;
}
public void setAccountNo(String accountNo) {
	this.accountNo = accountNo;
}
public Integer getCustomerId() {
	return customerId;
}
public void setCustomerId(Integer customerId) {
	this.customerId = customerId;
}
}
