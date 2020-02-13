package com.xiaohei.web.italker.push.factory;

import com.xiaohei.web.italker.push.bean.api.message.MessageCreateModel;
import com.xiaohei.web.italker.push.bean.db.Group;
import com.xiaohei.web.italker.push.bean.db.Message;
import com.xiaohei.web.italker.push.bean.db.User;
import com.xiaohei.web.italker.push.utils.Hib;

/****
 * 消息数据存储的类
 */
public class MessageFactory {
    /**
     * 查询某个消息
     */
    public static Message findById(String id){
        return Hib.query(session -> session.get(Message.class,id));
    }

    /***
     * 添加一条普通消息
     * @param sender
     * @param receiver
     * @param model
     * @return
     */
    public  static Message add(User sender,User receiver,MessageCreateModel model){
       Message message  = new Message(sender,receiver,model);
       return  save(message);
    }

    /***
     * 添加一条群信息
     * @param sender
     * @param receiver
     * @param model
     * @return
     */
 public  static Message add(User sender,Group receiver,MessageCreateModel model){
       Message message  = new Message(sender,receiver,model);
       return  save(message);
 }


    public  static  Message save(Message message){
        return Hib.query(session ->{
                    session.save(message);
                    session.flush();
                    session.refresh(message);
                    return message;
                }
        );
    }




}
