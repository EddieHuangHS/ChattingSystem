package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * 该类/对象，提供和消息相关的服务方法
 */
public class MessageClientService {
    /**
     *
     * @param content 内容
     * @param senderId 发送用户Id
     * @param getterId 接收用户Id
     */
    public void sendMessageToOne(String content, String senderId, String getterId){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setContent(content);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSendTime(new Date().toString()); // 发送时间设置到message对象
        System.out.println(senderId + " 对 " + getterId + " 说 " + content);
        // 发送给服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param content 发送内容
     * @param senderId 发送者
     */
    public void sendMessageToAll(String content, String senderId){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL); // 群发消息这种类型
        message.setContent(content);
        message.setSender(senderId);

        message.setSendTime(new Date().toString()); // 发送时间设置到message对象
        System.out.println(senderId + " 对所有人说 " + content);
        // 发送给服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
