package com.longhuapuxin.entity;

public class ResponsePhoto extends ResponseDad {
	public Photo Photo;
	
	public Photo getPhoto() {
		return Photo;
	}

	public void setPhoto(Photo photo) {
		Photo = photo;
	}

	public class Photo {
		String Id;
		String PhotoType;
		String FileName;
		String LabelCode;
		String SmallFileName;
		public String getId() {
			return Id;
		}
		public void setId(String id) {
			Id = id;
		}
		public String getPhotoType() {
			return PhotoType;
		}
		public void setPhotoType(String photoType) {
			PhotoType = photoType;
		}
		public String getFileName() {
			return FileName;
		}
		public void setFileName(String fileName) {
			FileName = fileName;
		}
		public String getLabelCode() {
			return LabelCode;
		}
		public void setLabelCode(String labelCode) {
			LabelCode = labelCode;
		}
		public String getSmallFileName() {
			return SmallFileName;
		}
		public void setSmallFileName(String smallFileName) {
			SmallFileName = smallFileName;
		}
	}

}
