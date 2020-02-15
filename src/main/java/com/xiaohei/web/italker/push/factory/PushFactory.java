package com.xiaohei.web.italker.push.factory;

import com.google.common.base.Strings;
import com.xiaohei.web.italker.push.bean.api.base.PushModel;
import com.xiaohei.web.italker.push.bean.card.MessageCard;
import com.xiaohei.web.italker.push.bean.db.*;
import com.xiaohei.web.italker.push.utils.Hib;
import com.xiaohei.web.italker.push.utils.PushDispatcher;
import com.xiaohei.web.italker.push.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/***
 * 消息的存储和处理的工具类
 */
public class PushFactory {
    /***
     * 发送一条消息 并在当前的发送历史中记录
     * @param sender
     * @param message
     */
    public static void pushNewMessage(User sender, Message message) {
        if (sender==null|| message==null){
            return;
        }
        //消息卡片用于发送
        MessageCard messageCard = new MessageCard(message);
        String entity = TextUtil.toJson(messageCard);
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();
        if (message.getGroup()==null&& Strings.isNullOrEmpty(message.getGroupId())){
        // 给朋友发送信息
            User receiver = UserFactory.foundById(message.getReceiverId());
            if (receiver==null)
               return;
            // 历史记录表字段建立
            PushHistory history = new PushHistory();
            history.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);// 普通新消息
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());

            PushModel model = new PushModel();
            // 每一条历史记录都是单独的可以单独发送
             model.add(history.getEntityType(),history.getEntity());
             // 把需要发送的数据丢给发送者发送
             dispatcher.add(receiver,model);
             // 保存发送的历史信息
            Hib.queryOnly(session -> session.save(history));

        }else{
            Group group = message.getGroup();
            if (group==null){
                group = GroupFactory.findById(sender, message.getGroupId());
            }
            if (group==null){
                return;
            }
        // 给群发送信息
            Set<GroupMember> members =GroupFactory.getMunbers(group);
            if (members==null|| members.size()==0){
                return;
            }
            // 过滤自己
            members.stream().filter(groupMember -> !groupMember.getUserId().equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());
            // 一个历史记录列表
            List<PushHistory> pushHistories = new ArrayList<>();
            addGroupMembersPushModel(dispatcher,pushHistories,members,entity,PushModel.ENTITY_TYPE_MESSAGE);
        }
        //发送者进行真是的提交
      dispatcher.submit();
    }

    /***
     * 给群成员构建一个消息，把消息存储到数据库中 ，每个人的每条消息都是一个记录
     * @param dispatcher
     * @param histories
     * @param members
     * @param entity
     * @param entityTypeMessage
     */
    private static void addGroupMembersPushModel(PushDispatcher dispatcher, List<PushHistory> histories, Set<GroupMember> members, String entity, int entityTypeMessage) {
          for (GroupMember member:members){
              User receiver = member.getUser();
              if (receiver==null)
                   return;
               // 历史记录表字段建立
               PushHistory pushhistory = new PushHistory();
              pushhistory.setEntityType(entityTypeMessage);// 普通新消息
              pushhistory.setEntity(entity);
              pushhistory.setReceiver(receiver);
              pushhistory.setReceiverPushId(receiver.getPushId());
              histories.add(pushhistory);
               PushModel model = new PushModel();
               model.add(pushhistory.getEntityType(),pushhistory.getEntity());
               dispatcher.add(receiver,model);
               Hib.queryOnly(session ->{
               for (PushHistory history : histories) {
                   session.saveOrUpdate(history);
               }
           });
          }
          dispatcher.submit();
    }
}
