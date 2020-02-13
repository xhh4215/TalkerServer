package com.xiaohei.web.italker.push.bean.api.message;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import com.xiaohei.web.italker.push.bean.api.user.UpdateInfoModel;
import com.xiaohei.web.italker.push.bean.db.Message;

/***
 * API请求model的格式
 */
public class MessageCreateModel {
    // ID从客户端生成 一个UUID
    @Expose
    private String id;
    // 消息内容
    @Expose
    private String content;
    // 附件
    @Expose
    private String attach;
    // 消息类型
    @Expose
    private int type = Message.TYPE_STR;

    // 接收者ID
    @Expose
    private String receiverId;
    //接受者类型 群 人
    @Expose
    private int receiverType = Message.RECEIVER_TYPE_NONE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }
    public static boolean check(MessageCreateModel model) {
        // Model 不允许为null，
        // 并且只需要具有一个及其以上的参数即可
        return model!=null &&
                !(Strings.isNullOrEmpty(model.id)
                ||Strings.isNullOrEmpty(model.content)
                ||Strings.isNullOrEmpty(model.receiverId))
                && (model.receiverType==Message.RECEIVER_TYPE_NONE|| model.receiverType == Message.RECEIVER_TYPE_GROUP)
                && (model.type==Message.TYPE_STR || model.receiverType == Message.TYPE_AUDIO || model.receiverType == Message.TYPE_FILE || model.receiverType == Message.TYPE_PIC);
    }

}
