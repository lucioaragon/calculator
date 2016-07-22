package com.lucioaragon.calculator.shared;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gwt.user.client.rpc.IsSerializable;

@PersistenceCapable
public class BinaryNumb implements IsSerializable{
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String convertedNumber;

	@Persistent
	private String date;
	
	@Persistent
	private String originalNumber;
	
	public BinaryNumb() {
		this.convertedNumber = null;
		this.originalNumber = null;
		this.date = null;
	}
	
	public BinaryNumb(String convertedNumber, String originalNumber, String date) {
		this.convertedNumber = convertedNumber;
		this.originalNumber = originalNumber;
		this.date = date;
	}
	
	
	// Accessors
	
	public void setConvertedNumber(String number) {
		this.convertedNumber = number;
	}

	public void setOriginalNumber(String number) {
		this.originalNumber = number;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getConvertedNumber() {
		return convertedNumber;
	}

	public String getOriginalNumber() {
		return originalNumber;
	}

	public String getDate() {
		return date;
	}

}
