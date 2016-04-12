package com.longhuapuxin.mqtt;

import java.util.ArrayList;

import org.apache.http.util.EncodingUtils;

public class MQTTMessage {
	



	public MessageTypeEnum MessageType;

	// / <summary>
	// / 是否是重复发送
	// / </summary>
	public boolean Dul;

	public QosEnum Qos;

	// / <summary>
	// / 是否保留
	// / </summary>
	public boolean Retain;

	public long BodyLength;

	public byte[] Body;

	public  byte[] MessageId;

	public String GetContent() {
		String result = EncodingUtils.getString(Body, "UTF-8");
		return result;
	}

	public void SetContent(String text) {
		if (text == null || text.length() <= 0) {
			Body = new byte[0];
		} else {
			Body = EncodingUtils.getBytes(text, "UTF-8");
		}
	}

	public boolean HasId(){
		return (Qos == QosEnum.AtLeastOne || Qos == QosEnum.OnlyOne)
				&& (MessageType == MessageTypeEnum.PUBLISH

				|| MessageType == MessageTypeEnum.PUBCOMP
				|| MessageType == MessageTypeEnum.PUBREC
				|| MessageType == MessageTypeEnum.PUBREL
		) || MessageType == MessageTypeEnum.PUBACK;
	}

	public byte[] ToBytes() {
		byte mark = (byte) 128;
		byte header = (byte) (MessageType.Val() << 4);
		ArrayList<Byte> lens = new ArrayList<Byte>();
		int l = Body == null ? 0 : Body.length;
		if (HasId()) {
			l += 2;//添加2个字节的messageId
		}

		int bodyLength = l;
		while (l > 127) {
			byte y = (byte) (l % 128);
			l = (l - y) / 128;
			lens.add(y);
		}
		lens.add((byte) l); 
		byte[] data = new byte[1 + lens.size() + bodyLength + 1];
		int idx = 0;
		data[idx++] = header;
		for (int i = lens.size() - 1; i > -1; i--) {
			if (i > 0) {
				data[idx++] = (byte) (lens.get(i) | mark);

			} else {
				data[idx++] = lens.get(i);
			}
		}
		if (HasId())
		{

			data[idx++] = MessageId[0];
			data[idx++] =  MessageId[1];
		}
		if(Body!=null) {
			for (int i = 0; i < Body.length; i++) {
				data[idx++] = Body[i];
			}
		}
		data[idx++] = (byte) 255;
		return data;
	}
}
