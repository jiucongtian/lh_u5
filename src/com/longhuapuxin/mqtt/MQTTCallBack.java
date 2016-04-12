package com.longhuapuxin.mqtt;

public interface MQTTCallBack {
	public void OnMessage(MQTTMessage msg, String socketId);
	public void OnNetWorkBreak(String errMessage);
	public void OnConnected();
}
