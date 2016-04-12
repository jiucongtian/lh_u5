package com.longhuapuxin.mqtt;
public enum QosEnum {
	AtMostOne(0), AtLeastOne(1), OnlyOne(2), Remain(3);
	// 定义私有变量
	@SuppressWarnings("unused")
	private int nCode;

	// 构造函数，枚举类型只能为私有
	private QosEnum(int _nCode) {

		this.nCode = _nCode;

	}
}