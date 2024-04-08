package qqclient.view;

import qqclient.service.*;
import qqclient.utils.Utility;
import qqcommon.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 客户端的菜单界面
 */
public class QQView {

    private boolean loop = true; // 控制是否显示菜单
    private String key = ""; // 接收用户的键盘输入
    private UserClientService userClientService = new UserClientService(); // 对象是用于登录服务/注册用户
    private MessageClientService messageClientService = new MessageClientService(); // 对象用户私聊/群聊
    private FileClientService fileClientService = new FileClientService(); // 对象传输文件

    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("客户端退出系统...");
    }

    // 显示主菜单
    private void mainMenu(){
        while(loop){
            System.out.println("========== 欢迎登录网络通信系统 ==========");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择：");
            key = Utility.readString(1);

            // 根据用户的输入，来处理不同的逻辑
            switch (key){
                case "1":
                    System.out.print("请输入用户号：");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密码：");
                    String pwd = Utility.readString(50);
                    // 需要到服务端去验证该用户是否合法
                    // 这里有很多代码，编写一个类UserClientService[用户登录/注册]
                    if(userClientService.checkUser(userId, pwd)){ // 还没有写完
                        System.out.println("========== 欢迎 (用户" + userId + "登陆成功) ==========");

                        // 遍历存放离线message的集合，查看是否有离线信息
                        // 这个方法没进-----------------
//                        if(OfflineSendNewsService.containsOffline(userId) == true){
//                            System.out.println(12312);
//                            // 找到对应getterId的离线消息队列
//                            ArrayList<Message> messages = OfflineSendNewsService.getMessage(userId);
//                            Iterator<Message> iterator = messages.iterator();
//                            // 遍历离线消息队列取得离线消息对象
//                            while(iterator.hasNext()){
//                                Message message = iterator.next();
//                                try {
//                                    ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(userId).getSocket().getOutputStream());
//                                    oos.writeObject(message);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            OfflineSendNewsService.removeOfflineUser(userId);
//                        }

                        // 进入到二级菜单
                        while(loop){
                            System.out.println("\n========== 网络通信系统二级菜单 (用户 " + userId + ") ==========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("请输入你的选择：");
                            key = Utility.readString(1);
                            switch (key){
                                case "1":
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.print("请输入想对大家说的话： ");
                                    String s = Utility.readString(100);
                                    // 调用一个方法，将消息封装成message对象，发送给服务端
                                    messageClientService.sendMessageToAll(s, userId);
                                    break;
                                case "3":
                                    System.out.print("请输入想聊天的用户号(在线)：");
                                    String gettedId = Utility.readString(50);
                                    System.out.print("请输入想说的话：");
                                    String content = Utility.readString(100);
                                    // 编写一个方法，将消息发送给服务端
                                    messageClientService.sendMessageToOne(content, userId, gettedId);
                                    break;
                                case "4":
                                    System.out.print("请输入你想把文件发送给的用户(在线)： ");
                                    String getterId = Utility.readString(50);
                                    System.out.print("请输入发送文件的路径(形式 d:\\xx.jpg)： ");
                                    String src = Utility.readString(100);
                                    System.out.print("请输入文件发送到对方电脑的路径(形式 d:\\xx.jpg)： ");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileToOne(src, dest, userId, getterId);
                                    break;
                                case "9":
                                    // 调用方法，给服务器发送一个退出系统的message
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }
                            try {
                                // 为了显示界面更美观
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{ // 登录服务器失败
                        System.out.println("========== 登陆失败 ==========");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }
}
