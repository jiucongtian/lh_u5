package com.longhuapuxin.mqtt;

//解析传过来的二进制，4步解析，4种状态。
public class MQTTReader {
	private static final String CMARK1 = "00000001";
	private static final String CMARK2 = "00000110";
	private static final String CMARK3 = "00001000";

	public String SocketID;

	public MQTTCallBack callback;

	private class StateEnum {
		public static final int WaitNew = 0;
		public static final int WaitLength = 1;
		public static final int WaitOtherPart = 2;
		public static final int WaitEnd = 3;
		public static final int WaitID1=4;
		public static final int WaitID2=5;
	}

	private int _mark1 = Byte.parseByte(CMARK1, 2);// Convert.ToByte(CMARK1, 2);
	private int _mark2 = Byte.parseByte(CMARK2, 2);
	private int _mark3 = Byte.parseByte(CMARK3, 2);
	private int _state = StateEnum.WaitNew;
	private MQTTMessage _message;
	private long _len = 0;
	private long _idx = 0;

	public MQTTReader(String socketId) {
		SocketID = socketId;
	}

	public void Write(byte b) {

		switch (_state) {
		case StateEnum.WaitNew:
			_message = new MQTTMessage();
			_message.Retain = (b & 0xff & _mark1) == 1;
			_message.Qos = QosEnum.values()[((b & 0xff & _mark2) >> 1)];
			_message.Dul = ((b & 0xff & _mark3) >> 3) == 1;
			_message.MessageType = MessageTypeEnum.values()[((b & 0xff) >> 4)];
			_state = StateEnum.WaitLength;
			_len = 0;
			_idx = 0;
			break;
		case StateEnum.WaitLength:
			if ((b & 0xff) >> 7 == 0) {
				_len = _len * 128 + b;
				_state = StateEnum.WaitOtherPart;
				if (_len == 0)
				{
					_state = StateEnum.WaitEnd;
				}
				if (_message.HasId())
				{
					_state = StateEnum.WaitID1;
					_len-=2;
				}

				_message.Body = new byte[(int) _len];
				if (_len <= 0) {
					_state = StateEnum.WaitEnd;
				}
			} else {
				_len = _len * 128 + ((b << 1 & 0xff) >> 1);
			}
			break;
			case StateEnum.WaitID1:
				_message.MessageId=new byte[2];
				_message.MessageId[0]=b;
				_state = StateEnum.WaitID2;
				break;
			case StateEnum.WaitID2:
				_message.MessageId[1]=b;
				_state = StateEnum.WaitOtherPart;
				if (_len == 0)
				{
					_state = StateEnum.WaitEnd;
				}
				break;
		case StateEnum.WaitOtherPart:
			_message.Body[(int) _idx++] = b;
			if (_idx == _len) {

				_state = StateEnum.WaitEnd;
			}

			break;
		case StateEnum.WaitEnd:
			if (b == -1) {
				_state = StateEnum.WaitNew;
				if (callback != null) {
					callback.OnMessage(_message, SocketID);

					_message = null;
				}

			}
			break;
		}
	}

	public void Write(byte[] data, int len) {
		for (int i = 0; i < len; i++) {
			Write(data[i]);
		}

	}
}
