package com.longhuapuxin.entity;

import java.util.List;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWho;

public class ResponseCare extends ResponseDad {

	List<CareWho> CareWho;

	public List<CareWho> getCareWho() {
		return CareWho;
	}

	public void setCareWho(List<CareWho> careWho) {
		CareWho = careWho;
	}
	
}
