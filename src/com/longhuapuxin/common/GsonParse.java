package com.longhuapuxin.common;

import com.google.gson.Gson;

public class GsonParse {
	static Gson gson;
	static {
		gson = new Gson();
	}

	public static Class<?> GsonParsing(String json, Class<?> myClass) {

		myClass = gson.fromJson(json, myClass.getClass());
		return myClass;
	}
}
