package com.xiaohei.web.italker.push.service;

import com.xiaohei.web.italker.push.bean.api.base.ResponseModel;
import com.xiaohei.web.italker.push.bean.api.message.MessageCreateModel;
import com.xiaohei.web.italker.push.bean.card.MessageCard;
import com.xiaohei.web.italker.push.bean.db.Group;
import com.xiaohei.web.italker.push.bean.db.Message;
import com.xiaohei.web.italker.push.bean.db.User;
import com.xiaohei.web.italker.push.factory.GroupFactory;
import com.xiaohei.web.italker.push.factory.MessageFactory;
import com.xiaohei.web.italker.push.factory.PushFactory;
import com.xiaohei.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
/***
 * 消息发送的入口
 */
@Path("/message")
public class MessageService extends BaseService {
    /***
     * 发送一条消息到服务器
     * @return
     */
    @POST
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel model){

        if(!MessageCreateModel.check(model)){
          return  ResponseModel.buildParameterError();
        }
        User self = getSelf();
        //查询是不是已经在数据库中存在
        Message message = MessageFactory.findById(model.getId());
        if(message!=null){
            return ResponseModel.buildOk(new MessageCard(message));

        }
        if (model.getReceiverType()== Message.RECEIVER_TYPE_GROUP){
             return pushToGroup(self,model);
        }else{
            return   pushToUser(self,model);
        }
    }

    /***
     * 发送到人
     * @param sender
     * @param model
     * @return
     */
    private ResponseModel<MessageCard> pushToUser(User sender, MessageCreateModel model) {
        User receiver = UserFactory.foundById(model.getId());
        if (receiver==null){
            return ResponseModel.buildNotFoundUserError("can't find receiver");
        }
        if (receiver.getId().equalsIgnoreCase(sender.getId())){
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        // 存储数据库
        Message message = MessageFactory.add(sender,receiver,model);
        return buildAndPushResponse(sender,message);
    }
    /***
     * 发送到群
     * @param sender
     * @param model
     * @return
     */
    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {
     // todo
        Group group = GroupFactory.findById(sender,model.getReceiverId());
        if (group==null){
            return ResponseModel.buildNotFoundUserError("can't find receiver group");
        }
        // 添加到数据库
        Message message = MessageFactory.add(sender,group,model);
        // 通用推送逻辑
        return buildAndPushResponse(sender,message);
    }
    /***
     * 推送并构建一个返回信息
     * @param sender
     * @param message
     * @return
     */
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {
        if (message==null){
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        // 进行推送
        PushFactory.pushNewMessage(sender,message);
        // 返回
        return   ResponseModel.buildOk(new MessageCard(message));
    }


}
