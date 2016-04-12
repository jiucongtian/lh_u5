package com.longhuapuxin.entity;

import android.graphics.Bitmap;
import android.widget.ImageView;
 

public class ResponseUploadFile extends ResponseDad {
	public Photo Photo;
	public class Photo {

		public int Id;

		public int PhotoType;

		public String FileName;
		
		public String SmallFileName;
		
		public int UserId;

		public String ShopCode;

		public String LabelCode;
	}
	 
}
