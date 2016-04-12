package com.longhuapuxin.entity;

import java.util.List;

import com.longhuapuxin.db.bean.BeanRecord;

public class ResponseGetBeanRecord extends ResponseDad {
	private List<BeanRecord> BeanRecords;
	private int Total;
	private double Balance;
	public List<BeanRecord> getBeanRecords() {
		return BeanRecords;
	}

	public void setBeanRecords(List<BeanRecord> beanRecords) {
		BeanRecords = beanRecords;
	}

	public int getTotal() {
		return Total;
	}

	public void setTotal(int total) {
		Total = total;
	}

	public double getBalance() {
		return Balance;
	}

	public void setBalance(double balance) {
		Balance = balance;
	}
}
