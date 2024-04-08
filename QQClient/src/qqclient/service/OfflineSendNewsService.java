package qqclient.service;

import qqcommon.Message;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class OfflineSendNewsService {
    private static ConcurrentHashMap<String, ArrayList<Message>> chm = new ConcurrentHashMap<>();

    // 添加给离线用户的消息
    public static void addOfflineMessage(String userId, Message message){
        if(!chm.containsKey(userId)){
            chm.put(userId, new ArrayList<Message>());
        }
        chm.get(userId).add(message);
    }

    public static ArrayList<Message> getMessage(String userId){
        return chm.get(userId);
    }

    public static boolean containsOffline(String userId){
        return chm.containsKey(userId);
    }

    public static void removeOfflineUser(String userId){
        chm.remove(userId);
    }
}
