package com.longhuapuxin.entity;

import java.util.List;

public class ResponseKeptLabel extends ResponseDad {

	List<KeptLabel> Labels;
	
	public List<KeptLabel> getLabels() {
		return Labels;
	}

	public void setLabels(List<KeptLabel> labels) {
		Labels = labels;
	}

	public class KeptLabel {
		String Code;
		String Name;
		String CategoryCode;
		public String getCode() {
			return Code;
		}
		public void setCode(String code) {
			Code = code;
		}
		public String getName() {
			return Name;
		}
		public void setName(String name) {
			Name = name;
		}
		public String getCategoryCode() {
			return CategoryCode;
		}
		public void setCategoryCode(String category) {
			CategoryCode = category;
		}
	}
}
