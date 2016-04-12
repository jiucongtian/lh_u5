package com.longhuapuxin.entity;

import java.util.List;

public class ResponseLabelCategory extends ResponseDad {
	List<LabelCategory> LabeCategories;
	
	public List<LabelCategory> getLabelCategories() {
		return LabeCategories;
	}

	public void setLabelCategories(List<LabelCategory> labelCategories) {
		LabeCategories = labelCategories;
	}

	public class LabelCategory {
		String Code;
		String Name;
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
	}
}
