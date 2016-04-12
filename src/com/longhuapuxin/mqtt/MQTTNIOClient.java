package com.longhuapuxin.mqtt;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by lsy on 2016/2/14.
 */
public class MQTTNIOClient  {
    /** 信道选择器 */
    private Selector mSelector;
    private ArrayList<String> _topics;
    /** 服务器通信的信道 */
    private SocketChannel mChannel;



    /** 是否加载过的标识 */
    private boolean mIsInit = false;

    /** 单键实例 */
    private static MQTTNIOClient gTcp;

    private  MQTTReader mqttReader;

    /** 默认链接超时时间 */
    public static final int TIME_OUT = 10000;

    /** 读取buff的大小 */
    public static final int READ_BUFF_SIZE = 1024;



    public static synchronized MQTTNIOClient instance(String host, int port, MQTTCallBack callBack) {
        if ( gTcp == null ) {
            gTcp = new MQTTNIOClient(host,port,callBack);
        }
        return gTcp;
    }




    public void Connect() {

        //需要在子线程下进行链接
        MyConnectRunnable connect = new MyConnectRunnable();
        new Thread(connect).start();
    }



    public Selector getSelector() {
        return mSelector;
    }


    public boolean isConnect() {

        return mIsInit && mChannel.isConnected();
    }




    /**
     * 每次读完数据后,需要重新注册selector读取数据
     * @return
     */
    private synchronized boolean repareRead() {
        boolean bRes = false;
        try {
            //打开并注册选择器到信道
            mSelector = Selector.open();
            if ( mSelector != null ) {
                mChannel.register(mSelector, SelectionKey.OP_READ);
                bRes = true;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return bRes;
    }

    public void revMsg() {
        if ( mSelector == null ) {
            return;
        }
        boolean bres = true;
        while ( mIsInit ) {
            if ( !isConnect() ) {
                bres = false;
            }
            if ( !bres ) {
                try {
                    Thread.sleep(100);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }

                continue;
            }

            try {
                //有数据就一直接收
                while (mIsInit && mSelector.select() > 0) {
                    for ( SelectionKey sk : mSelector.selectedKeys() ) {
                        //如果有可读数据
                        if ( sk.isReadable() ) {
                            //使用NIO读取channel中的数据
                            SocketChannel sc = (SocketChannel)sk.channel();
                            //读取缓存
                            ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFF_SIZE);
                            //实际的读取流
                            ByteArrayOutputStream read = new ByteArrayOutputStream();
                            int nRead = 0;

                            //单个读取流
                            byte[] bytes;
                            //读完为止
                            while ( (nRead = sc.read(readBuffer) ) > 0 ) {
                                //整理
                                readBuffer.flip();
                                bytes = new byte[nRead];

                                //将读取的数据拷贝到字节流中
                                readBuffer.get(bytes);
                                //将字节流添加到实际读取流中
                                read.write(bytes);

                                mqttReader.Write(bytes, nRead);
                                readBuffer.clear();

                            }


                            //为下一次读取做准备
                            sk.interestOps(SelectionKey.OP_READ);
                        }

                        //删除此SelectionKey
                        mSelector.selectedKeys().remove(sk);
                    }
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 链接线程
     * @author HeZhongqiu
     *
     */
    private class MyConnectRunnable implements Runnable {

        @Override
        public void run() {
            Boolean silence=false;
            try {
                //打开监听信道,并设置为非阻塞模式

                SocketAddress ad = new InetSocketAddress(_host, _port);
                if (_topics == null) {
                    _topics = new ArrayList<String>();
                }
                _topics.clear();
                mChannel = SocketChannel.open( ad );
                if ( mChannel != null ) {
                    mChannel.socket().setTcpNoDelay(false);
                    mChannel.socket().setKeepAlive(false);

                    //设置超时时间
                    mChannel.socket().setSoTimeout(TIME_OUT);
                    mChannel.configureBlocking(false);

                    mIsInit = repareRead();

                    //创建读线程
                    RevMsgRunnable rev = new RevMsgRunnable();
                    new Thread(rev).start();
                    new Thread(heartRunnable).start();
                    Connected=true;
                    if (callBack != null) {

                        callBack.OnConnected();
                    }
                    SendMessage(MessageTypeEnum.CONNECT);
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                if (callBack != null) {

                    callBack.OnNetWorkBreak(e.getMessage());
                    silence=true;
                }

            } finally {
                if ( !mIsInit ) {
                    Close(silence);
                }
            }
        }
    }

    private class RevMsgRunnable implements Runnable {

        @Override
        public void run() {

            revMsg();
        }

    }

    private String _host;
    private int _port;

    public MQTTCallBack callBack;
    public boolean Connected;

    private int ping = 0;
    private int pang = 0;
    private Runnable heartRunnable = new Runnable() {

        @Override
        public void run() {
            while (Connected) {
                if (!mIsInit) {
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

    public MQTTNIOClient(String host, int port, MQTTCallBack callBack) {
        this.callBack = callBack;
        this._host = host;
        this._port = port;
        String socketId = UUID.randomUUID().toString();

        mqttReader = new MQTTReader(socketId);
        mqttReader.callback = _msgHandler;

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
        try{
            if(mChannel!=null && mIsInit){
                MQTTMessage msg = new MQTTMessage();
                msg.MessageType = MessageTypeEnum.DISCONNECT;
                byte[] data = msg.ToBytes();
                ByteBuffer buf = ByteBuffer.wrap(data);
                  mChannel.write(buf);

            }
        }
        catch (Exception e){}

        mIsInit = false;

        _topics.clear();
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
                        mIsInit = false;

                        try {
                            if ( mSelector != null ) {
                                mSelector.close();
                            }
                            if ( mChannel != null ) {
                                mChannel.close();
                            }
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    } catch (Exception ex) {

                    }

                    OnNetWorkBreak("关闭连接");

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

    private boolean SendData(byte[] data) {
        boolean bRes = false;
        if ( !mIsInit ) {

            return false;
        }
        try {
            ByteBuffer buf = ByteBuffer.wrap(data);
            int nCount = mChannel.write(buf);
            if ( nCount > 0 ) {
                bRes = true;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return bRes;

    }


}
