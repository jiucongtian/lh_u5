package com.longhuapuxin.mqtt;

public enum MessageTypeEnum {
	// 暂留
	Reserved(0),
	// 客戶端向服务器发送链接
	CONNECT(1),
	// 服务器向客戶端发送response
	CONNACK(2),
	// 发消息
	PUBLISH(3),
	// 发消息的response
	PUBACK(4),
	// 暂留
	PUBREC(5),
	// 暂留
	PUBREL(6),
	// 暂留
	PUBCOMP(7),
	// 订阅话题（customer_+客戶端userId）,唯一标识
	SUBSCRIBE(8),
	// response
	SUBACK(9),
	// 取消订阅（暂留），apllication退出
	UNSUBSCRIBE(10), UNSUBACK(11),
	// 心跳包
	PINGREQ(12),
	// /
	// /PING Response
	// /
	PINGRESP(13),
	// /
	// /Client is Disconnecting
	// /
	DISCONNECT(14),
	// 暂留
	Reserved2(15);
	// 定义私有变量
	private int nCode;

	// 构造函数，枚举类型只能为私有
	private MessageTypeEnum(int _nCode) {

		this.nCode = _nCode;

	}

	public int Val() {
		return nCode;
	}
}
