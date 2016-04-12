package com.longhuapuxin.mqtt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import android.util.Log;



public class MQTTClient implements Runnable {
	private String _host;
	private int _port;
	private Socket _socket;
	private ArrayList<String> _topics;
	private Thread _receiveThread;
	private OutputStream _output;
	public MQTTCallBack callBack;
	public boolean Connected;
	public final static int ReadMessage = 1;
	private InputStream _input;
	private int ping = 0;
	private int pang = 0;
	private Runnable heartRunnable = new Runnable() {

		@Override
		public void run() {
			while (Connected) {
				if (_socket == null) {
					Connected = false;
					if (callBack != null) {

						callBack.OnNetWorkBreak("连接已经断开");

					}
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 针对发送
				ping++;
				// 针对接收
				pang++;
				if (ping >= 30) {
					SendMessage(MessageTypeEnum.PINGREQ);
				}
				if (pang > 60) {
					Connected = false;
					if (callBack != null) {

						callBack.OnNetWorkBreak("服务器长时间没响应");

					}
					break;
				}
			}
			Log.d("MQTT", "heart break stoped");
		}

	};

	public MQTTClient(String host, int port, MQTTCallBack callBack) {
		this.callBack = callBack;
		this._host = host;
		this._port = port;
		Connect();
	}

	public void Connect() {
		ping = 0;
		pang = 0;
		if (_topics == null) {
			_topics = new ArrayList<String>();
		}
		_topics.clear();
		try {
			_socket = new Socket();
			_socket.connect(new InetSocketAddress(_host, _port), 5000);
			_socket.setSoLinger(true, 3000);
			_socket.setTcpNoDelay(true);
			_socket.setSoTimeout(1000);
			_socket.setKeepAlive(false);

			_output = _socket.getOutputStream();// new
			_input = _socket.getInputStream();
			Connected = true;
			if (_output != null && _input != null) {

				_receiveThread = new Thread(this);
				_receiveThread.start();
				new Thread(heartRunnable).start();
			}

			SendMessage(MessageTypeEnum.CONNECT);

			if (callBack != null) {

				callBack.OnConnected();
			}

		}

		catch (Exception ex) {
			Connected = false;

			if (callBack != null) {

				callBack.OnNetWorkBreak(ex.getMessage());

			}

		}

	}

	private void SendMessage(MessageTypeEnum msgType) {
		MQTTMessage msg = new MQTTMessage();
		msg.MessageType = msgType;
		byte[] data = msg.ToBytes();
		SendData(data);
		ping = 0;
	}

	private void SendMessage(MessageTypeEnum msgType, String msgBody) {
		MQTTMessage msg = new MQTTMessage();
		msg.MessageType = msgType;
		msg.SetContent(msgBody);
		byte[] data = msg.ToBytes();
		SendData(data);
		ping = 0;
	}

	public void Close(boolean silence) {

		Connected = false;
		try {
			if (_socket != null && _socket.isConnected()) {
				MQTTMessage msg = new MQTTMessage();
				msg.MessageType = MessageTypeEnum.DISCONNECT;
				byte[] data = msg.ToBytes();

				_output.write(data, 0, data.length);
				_output.flush();
				_socket.shutdownInput();
				_socket.shutdownOutput();

				_socket.close();
				_output.close();
				_input.close();
				_topics.clear();
			}
		} catch (Exception ex) {

		} finally {
			_socket = null;

		}
		if (_receiveThread != null && _receiveThread.isAlive()) {

			_receiveThread.interrupt();

			_receiveThread = null;
		}
		if (silence) {
			return;
		}
		if (callBack != null) {

			callBack.OnNetWorkBreak("关闭连接");

		}

	}

	private MQTTCallBack _msgHandler = new MQTTCallBack() {
		// 有response
		@Override
		public void OnMessage(MQTTMessage msg, String socketId) {
			pang = 0;
			switch (msg.MessageType) {
			case CONNACK:

				if (callBack != null) {
					callBack.OnMessage(msg, socketId);
				}
				break;
			case PUBLISH:
				if (callBack != null) {
					callBack.OnMessage(msg, socketId);
				}
				if (msg.HasId())
				{
					MQTTMessage backMsg = new MQTTMessage();

					backMsg.MessageType = MessageTypeEnum.PUBACK;
					backMsg. Qos = msg.Qos;
					backMsg. MessageId = msg.MessageId;
					SendData(backMsg.ToBytes());

				}


				break;
			case DISCONNECT:

				Connected = false;
				try {
					if (_socket != null && _socket.isConnected()) {

						_socket.shutdownInput();
						_socket.shutdownOutput();

						_socket.close();
						_output.close();
						_input.close();

					}
				} catch (Exception ex) {

				}
				if (_receiveThread != null && _receiveThread.isAlive()) {

					_receiveThread.interrupt();

					_receiveThread = null;
				}
				if (callBack != null) {
					callBack.OnNetWorkBreak("关闭连接");
				}

				break;
			default:
				break;
			}
		}

		@Override
		public void OnNetWorkBreak(String errMessage) {
			if (callBack != null) {
				callBack.OnNetWorkBreak(errMessage);
			}
		}

		@Override
		public void OnConnected() {
			if (callBack != null) {
				callBack.OnConnected();
			}
		}

	};

	private void ReceiveMessage() {
		String sockeId = UUID.randomUUID().toString();

		MQTTReader mqttReader = new MQTTReader(sockeId);
		mqttReader.callback = _msgHandler;
		int size = 1024;
		byte[] buff = new byte[size];

		int receiveNumber ;
		try {
			// 通过clientSocket接收数据
			while (Connected) {
				try {
					receiveNumber = _input.read(buff);
				} catch (java.net.SocketTimeoutException sockettimeoutex) {
					continue;
				}
				if (receiveNumber > 0) {
					try {
						mqttReader.Write(buff, receiveNumber);
					} catch (Exception e) {

					}
				}
			}
		} catch (Exception ex) {
			Close(false);
		}

	}

	public void Subscribe(String[] topics) throws IOException {
		if (topics.length == 0)
			return;
		String msgbody = topics[0];
		for (int i = 1; i < topics.length; i++) {
			msgbody += "," + topics[i];
		}
		SendMessage(MessageTypeEnum.SUBSCRIBE, msgbody);

	}

	public void Subscribe(String topic) {

		if (topic == null || topic.length() <= 0) {
			return;
		}
		if (!_topics.contains(topic)) {
			SendMessage(MessageTypeEnum.SUBSCRIBE, topic);

			_topics.add(topic);
		}

	}

	public void Pubilsh(String topic, String msg) {
		SendMessage(MessageTypeEnum.PUBLISH, topic + "," + msg);

	}

	private void SendData(byte[] data) {
		try {
			_output.write(data, 0, data.length);
			_output.flush();

		} catch (Exception ex) {
			Close(false);
		}
	}

	@Override
	public void run() {
		ReceiveMessage();
	}
}
