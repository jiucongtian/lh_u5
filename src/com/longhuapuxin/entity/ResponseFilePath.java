package com.longhuapuxin.entity;

import java.util.List;

public class ResponseFilePath extends ResponseDad {

	List<FileObject> Files;
	
	public List<FileObject> getFiles() {
		return Files;
	}

	public void setFiles(List<FileObject> files) {
		Files = files;
	}

	public class FileObject {
		String Id;
		String Original;
		String Small;
		public String getId() {
			return Id;
		}
		public void setId(String id) {
			Id = id;
		}
		public String getOriginal() {
			return Original;
		}
		public void setOriginal(String original) {
			Original = original;
		}
		public String getSmall() {
			return Small;
		}
		public void setSmall(String small) {
			Small = small;
		}
		
	}
}
