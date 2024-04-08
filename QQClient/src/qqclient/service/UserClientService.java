package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成用户登录验证和用户注册等功能
 */
public class UserClientService {

    private User u = new User(); // 因为我们可能在其他地方使用user信息，因此做成成员属性
    // 因为socket在其他地方也可能使用，因此做出属性
    private Socket socket;


    // 根据userId和pwd验证该用户是否有效
    public boolean checkUser(String userId, String pwd){
        boolean b = false;
        // 创建User对象
        u.setUserId(userId);
        u.setPassword(pwd);

        try {
            // 连接到服务端，发送User对象
            Socket socket = new Socket(InetAddress.getByName("192.168.137.1"), 9999);
            // 得到ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u); // 发送User对象

            // 读取从服务端回复的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();

            if(ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)){ // 登录成功
                // 创建一个和服务器端保持通信的线程->创建一个类ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                // 启动客户端的线程
                clientConnectServerThread.start();
                // 这里为了后面客户端的拓展，我们将线程放入到集合管理
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                b = true;
            }else{ // 登录失败
                // 如果登录失败，我们就不能启动和服务器通信的线程
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    // 向服务器端请求在线用户列表
    public void onlineFriendList(){

        // 发送一个Message，类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        // 发送给服务器
        // 得到当前线程的socket对应的ObjectOutputStream对象
        try {
            // 高并发情况下下面的这个代码会出现问题，所以用紧接着长度更长的代码来完成传输
            // 以下这一句会报错，因为socket没有被构造器初始化
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            // 从管理线程的集合中，通过userId，得到这个线程
            ClientConnectServerThread clientConnectServerThread = ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId());
            // 通过这个线程得到关联的socket
            Socket socket = clientConnectServerThread.getSocket();
            // 得到当前线程的Socket对应的ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            // 发送一个Message对象，向服务端要求在线用户列表
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 编写方法，退出客户端并给服务端发送一个退出系统的message对象
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId()); // 一定要指定是哪个客户端

        try {
            // 发送message
            // 下一句的输出流方式会报错，因为socket没有被构造器初始化
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId() + " 退出系统");
            System.exit(0); // 结束进程，会让所有的子进程结束
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
