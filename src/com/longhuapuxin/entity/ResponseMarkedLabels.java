package com.longhuapuxin.entity;

import java.util.List;

import com.longhuapuxin.entity.ResponseGetAccount.User.Label;

public class ResponseMarkedLabels extends ResponseDad {

	List<Label> Labels;
	
	public List<Label> getLabels() {
		return Labels;
	}

	public void setLabels(List<Label> labels) {
		Labels = labels;
	}
}
