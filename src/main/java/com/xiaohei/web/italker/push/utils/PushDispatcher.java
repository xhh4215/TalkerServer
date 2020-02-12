package com.xiaohei.web.italker.push.utils;

import com.gexin.rp.sdk.base.IBatch;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.google.common.base.Strings;
import com.xiaohei.web.italker.push.bean.api.base.PushModel;
import com.xiaohei.web.italker.push.bean.db.User;
import org.hibernate.engine.jdbc.batch.spi.Batch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 * 消息推送工具类
 */
public class PushDispatcher {
            // 详见【概述】-【服务端接入步骤】-【STEP1】说明，获得的应用配置
      private final static String host = "http://sdk.open.api.igexin.com/apiex.htm";
      private static String appId = "d52jTJlSHX8DeW0N76Duk9";
      private static String appKey = "bood3K5H0W9hSqhMWF7Hu9";
      private static String masterSecret = "2ECw2EQOYu8fkUNLcpRVB2";
      private IGtPush pusher;
      /***
       * 要收到消息的人和内容的列表
       */
      private List<BatchBean> beans = new ArrayList<>();
      public PushDispatcher(){
            // 最基本的发送者
            pusher = new IGtPush(host, appKey, masterSecret);

      }

      /***
       * 官方文档发送穿透信息
       * @param cid  pushid
       * @param msg  发送的信息
       * @param batch  批量发送的容器
       * @throws Exception
       */
      private static void constructClientTransMsg(String cid, String msg ,IBatch batch) throws Exception {
                  SingleMessage message = new SingleMessage();
                  TransmissionTemplate template = new TransmissionTemplate();
                  template.setAppId(appId);
                  template.setAppkey(appKey);
                  template.setTransmissionContent(msg);
                  template.setTransmissionType(2); // 透传消息接受方式设置，1：立即启动APP，2：客户端收到消息后需要自行处理

                  message.setData(template);// 设置透传信息
                  message.setOffline(true); //是否可以离线发送
                  message.setOfflineExpireTime(24 * 3600*1000);// 离线消息的时常
                  // 设置推送目标，填入appid和clientId
                  Target target = new Target();
                  target.setAppId(appId);
                  target.setClientId(cid);
                  // 可以添加多个单独的推送任务
                  batch.add(message, target);
            }

      /***
       * 官方文档的发送通知信息
       * @param cid  pushid
       * @param msg  发送的信息
       * @param batch  批量发送的容器
       * @throws Exception
       */
      private static void constructClientLinkMsg(String cid, String msg ,IBatch batch) throws Exception {

                  SingleMessage message = new SingleMessage();
                  LinkTemplate template = new LinkTemplate();
                  // 设置APPID与APPKEY
                  template.setAppId(appId);
                  template.setAppkey(appKey);

                  Style0 style = new Style0();
                  // 设置通知栏标题与内容
                  style.setTitle("请输入通知栏标题");
                  style.setText("请输入通知栏内容");
                  // 配置通知栏图标
                  style.setLogo("icon.png");
                  // 配置通知栏网络图标
                  style.setLogoUrl("");
                  // 设置通知是否响铃，震动，或者可清除
                  style.setRing(true);
                  style.setVibrate(true);
                  style.setClearable(true);
                  template.setStyle(style);

                  message.setData(template);
                  message.setOffline(true);
                  message.setOfflineExpireTime(360 * 1000);

                  // 设置推送目标，填入appid和clientId
                  Target target = new Target();
                  target.setAppId(appId);
                  target.setClientId(cid);
                  batch.add(message, target);
            }

      /***
       * 添加批量发送的数据
        * @param receiver  数据接受者
       * @param model  推送的数据model
       * @return  是否添加成功
       */
      public boolean add(User receiver, PushModel model){
            if (receiver==null || Strings.isNullOrEmpty(receiver.getPushId())|| model==null){
                  return false;
            }
            // 推送的数据信息
            String  pusherString = model.getPushString();
            if (Strings.isNullOrEmpty(pusherString)){
                  return  false;
            }

            BatchBean bean = buildMessage( receiver.getPushId(),pusherString );
            beans.add(bean);
            return true;

      }

      /***
       * 对要发送的数据格式化封装
       * @param clientId  接受者id
       * @param text 发送的文本
       * @return 格式化的数据
       */
      public BatchBean buildMessage(String clientId, String text){
            TransmissionTemplate template = new TransmissionTemplate();
            template.setAppId(appId);
            template.setAppkey(appKey);
            template.setTransmissionContent(text);
            template.setTransmissionType(0); // 透传消息接受方式设置，1：立即启动APP，2：客户端收到消息后需要自行处理

            SingleMessage message = new SingleMessage();
            message.setData(template);// 设置透传信息
            message.setOffline(true); //是否可以离线发送
            message.setOfflineExpireTime(24 * 3600*1000);// 离线消息的时常
            // 设置推送目标，填入appid和clientId
            Target target = new Target();
            target.setAppId(appId);
            target.setClientId(clientId);
            return new BatchBean(message,target);

      }
      /***
       * 给每个人发送消息的一个bean
       */
      public static class BatchBean{
                  SingleMessage message;
                  Target target;
                  BatchBean(SingleMessage message, Target target) {
                        this.message = message;
                        this.target = target;
                  }
            }

      /***
       * 进行消息发送
        */
      public boolean submit(){
        IBatch  batch = pusher.getBatch();
        // 是否有数据需要发送
        boolean  haveData = false;
        for (BatchBean bean:beans){
              try{
                batch.add(bean.message,bean.target);
                haveData = true;
              }catch (Exception e){
                    e.printStackTrace();
              }

        }
        if (!haveData){
              return false;
        }

            IPushResult result = null;
        try{
            result = batch.submit();
        }catch (Exception e){
              e.printStackTrace();
              //失败尝试重复发送
              try{
                    batch.retry();
              }catch (Exception e1){
                    e1.printStackTrace();
              }

        }
        if (result!=null){
              Logger.getLogger("PushDispatcher").log(Level.INFO,(String) result.getResponse().get("result"));
              return true;
        }
            Logger.getLogger("PushDispatcher").log(Level.WARNING,"服务器有问题");
             return false;
      }



}
