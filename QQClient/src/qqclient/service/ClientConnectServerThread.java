package qqclient.service;

import qqclient.utils.Utility;
import qqcommon.Message;
import qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread{
    // 该线程需要持有Socket
    private Socket socket;

    // 构造器可以接收一个Socket对象
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        // 因为Thread需要在后台和服务器通信，因此我们while循环
        while(true){
            try {
                System.out.println("客户端线程，等待读取从服务器端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 如果服务器端没有发送Message对象，线程会阻塞
                Message message = (Message) ois.readObject();

                // 判断这个message类型，然后做对应的业务处理
                if(message.getMesType().equals(MessageType.MESSAGE_RETURN_ONLINE_FRIEND)){
                    // 取出在线列表信息，并显示
                    // 规定
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n==========当前在线用户列表==========");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户： " + onlineUsers[i]);
                    }
                }else if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    // 把服务器转发的消息，显示到控制台
                    System.out.println("\n" + message.getSender() + " 对 " + message.getGetter() + " 说： " + message.getContent());
                }else if(message.getMesType().equals(MessageType.MESSAGE_TO_ALL)){
                    // 显示在客户端的控制台
                    System.out.println("\n" + message.getSender() + " 对所有人说 " + message.getContent());
                }else if(message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){ // 如果是文件消息
//                    System.out.println("\n" + message.getSender() + " 给 " + message.getGetter()
//                     + " 发文件： " + message.getSrc() + " 到我的电脑的目录 " + message.getDest());

//                    // 作为接收方，可以自己设定文件传输的位置 尚未实现
//                    System.out.print("\n请输入你想保存文件的路径(形式 d:\\xx.jpg)： ");
//                    String dest = Utility.readString(100);
//                    if(dest.length() != 0){ // 当接收方制定了传输位置，如果接收方没有指定，则按发送方指定的传输位置来输出
//                        message.setDest(dest);
//                    }

                    // 取出message的文件字节数组，通过文件输出流写出到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n 保存文件成功~");
                }else{
                    System.out.println("是其他类型的message，暂时不处理...");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    // 为了更方便得到Socket
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
