package com.longhuapuxin.entity;

import java.util.List;

public class ResponseTopLabel extends ResponseDad {

	List<TopLabel> MarkTopten;
	
	public List<TopLabel> getMarkTopten() {
		return MarkTopten;
	}

	public void setMarkTopten(List<TopLabel> markTopten) {
		MarkTopten = markTopten;
	}

	public class TopLabel {
		String LabelName;
		String LabelCode;
		String Count;
		String Photo;
		public String getLabelName() {
			return LabelName;
		}
		public void setLabelName(String labelName) {
			LabelName = labelName;
		}
		public String getLabelCode() {
			return LabelCode;
		}
		public void setLabelCode(String labelCode) {
			LabelCode = labelCode;
		}
		public String getCount() {
			return Count;
		}
		public void setCount(String count) {
			Count = count;
		}
		public String getPhoto() {
			return Photo;
		}
		public void setPhoto(String photo) {
			Photo = photo;
		}
	}
}
