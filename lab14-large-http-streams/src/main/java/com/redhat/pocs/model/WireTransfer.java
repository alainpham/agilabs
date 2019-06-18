package com.redhat.pocs.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
@CsvRecord( separator = ",")
public class WireTransfer implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	@DataField(pos = 1)
	private Long paymentDate;
	@DataField(pos = 2)
	private String currency;
	@DataField(pos = 3,precision = 2)
	private BigDecimal amount;
	@DataField(pos = 4)
	private String sendersName;
	@DataField(pos = 5)
	private String senderAddress;
	@DataField(pos = 6)
	private String sendersAccountNb;
	@DataField(pos = 7)
	private String beneficiaryName;
	@DataField(pos = 8)
	private String beneficiaryAddress;
	@DataField(pos = 9)
	private String beneficiaryAccountNb;
	@DataField(pos = 10)
	private String detailsOfCharge;

	
	public Long getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Long paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getSendersName() {
		return sendersName;
	}
	public void setSendersName(String sendersName) {
		this.sendersName = sendersName;
	}
	public String getSenderAddress() {
		return senderAddress;
	}
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	public String getSendersAccountNb() {
		return sendersAccountNb;
	}
	public void setSendersAccountNb(String sendersAccountNb) {
		this.sendersAccountNb = sendersAccountNb;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	public String getBeneficiaryAddress() {
		return beneficiaryAddress;
	}
	public void setBeneficiaryAddress(String beneficiaryAddress) {
		this.beneficiaryAddress = beneficiaryAddress;
	}
	public String getBeneficiaryAccountNb() {
		return beneficiaryAccountNb;
	}
	public void setBeneficiaryAccountNb(String beneficiaryAccountNb) {
		this.beneficiaryAccountNb = beneficiaryAccountNb;
	}
	public String getDetailsOfCharge() {
		return detailsOfCharge;
	}
	public void setDetailsOfCharge(String detailsOfCharge) {
		this.detailsOfCharge = detailsOfCharge;
	}
	
	
	
	
	
}
