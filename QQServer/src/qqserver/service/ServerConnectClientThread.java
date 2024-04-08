package qqserver.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类的一个对象和某个客户端保持通信
 */
public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userId; // 连接到服务端的用户Id

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() { // 这里线程处于run的状态，可以发送/接收消息
        while(true){
            try {
                System.out.println("服务端与客户端" + userId + "保持通信，读取数据...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message)ois.readObject();

                // 根据message的类型，做相应的业务处理
                if(message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                    // 客户端请求在线用户列表
                    System.out.println(message.getSender() + " 请求在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    // 准备返回message
                    // 构建一个message对象，返回给客户端
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RETURN_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    // 返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);
                }else if(message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)){
                    // 客户端退出
                    System.out.println(message.getSender() + " 退出");
                    // 将这个客户端对应的线程从集合删除
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    socket.close(); // 关闭连接
                    break; // 退出while循环
                }else if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    String getterId = message.getGetter();
                    // 当管理线程的集合包含在线的线程时再传输
                    if(ManageClientThreads.containsOnline(getterId)){
                        // 根据message获取getter id，然后再得到对应线程
                        ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(getterId);
                        ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        oos.writeObject(message); // 转发，提示如果客户不在线，可以保存到数据库，就可以实现离线留言
                    }else{
                        OfflineSendNewsService.addOfflineMessage(getterId, message);
                    }

                }else if(message.getMesType().equals(MessageType.MESSAGE_TO_ALL)){
                    // 需要遍历管理线程的集合，把所有线程的socket都得到，然后把message进行转发即可
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while(iterator.hasNext()){
                        // 取出在线用户的id
                        String onlineUserId = iterator.next().toString();
                        if(!onlineUserId.equals(message.getSender())){ // 排除自身
                            // 进行转发
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                }else if(message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    String getterId = message.getGetter();
                    if(ManageClientThreads.containsOnline(getterId)){
                        // 根据getter id 获取到对应的线程，将message对象转发
                        ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                        ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        // 转发
                        oos.writeObject(message);
                    }else{
                        OfflineSendNewsService.addOfflineMessage(getterId, message);
                    }




                }else{
                    System.out.println("其他类型的message，暂时不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
