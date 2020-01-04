package com.xiaohei.web.italker.push.service;

import com.google.common.base.Strings;
import com.xiaohei.web.italker.push.bean.api.account.AccountRspModel;
import com.xiaohei.web.italker.push.bean.api.account.LoginModel;
import com.xiaohei.web.italker.push.bean.api.account.RegisterModel;
import com.xiaohei.web.italker.push.bean.api.base.ResponseModel;
import com.xiaohei.web.italker.push.bean.db.User;
import com.xiaohei.web.italker.push.factory.UserFactory;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author qiujuer
 */
// 127.0.0.1/api/account/...
@Path("/account")
public class AccountService extends BaseService {

    // 登陆
    @POST
    @Path("/login")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> login(LoginModel model) {
       if(! LoginModel.check(model)){
           return ResponseModel.buildParameterError();
       }
       User user = UserFactory.Login(model.getAccount(),model.getPassword());
       if (user!=null){
           if (!Strings.isNullOrEmpty(model.getPushId())){
               return bind(user,model.getPushId());
           }
           AccountRspModel accountRspModel = new AccountRspModel(user);
           return ResponseModel.buildOk(accountRspModel);
       }else{
           return ResponseModel.buildLoginError();
       }
    }


    // 注册
    @POST
    @Path("/register")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> register(RegisterModel model) {
        if(! RegisterModel.check(model)){
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.foundByPhone(model.getAccount().trim());
        if (user != null) {
            return ResponseModel.buildHaveAccountError();
        }
        user = UserFactory.foundByName(model.getName().trim());
        if (user != null) {
            return ResponseModel.buildHaveNameError();
        }
        user = UserFactory.register(model.getAccount(), model.getName(), model.getPassword());
        if (user != null) {
            if (!Strings.isNullOrEmpty(model.getPushId())){
                return bind(user,model.getPushId());
            }
            AccountRspModel accountRspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(accountRspModel);

        } else {
            return ResponseModel.buildRegisterError();
        }
    }

    // 绑定
    @POST
    @Path("/bind/{pushId}")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> bind(@PathParam("pushId") String pushId) {
        if( Strings.isNullOrEmpty(pushId)){
            return ResponseModel.buildParameterError();
        }
//        User user = UserFactory.foundByToken(token);
        User self = getSelf();
        return bind(self,pushId);
    }

    private ResponseModel<AccountRspModel> bind(User self,String pushId){
      User  user =   UserFactory.bindPushId(self,pushId);
        if (user==null) {
            return ResponseModel.buildServiceError();
        }else {
            AccountRspModel accountRspModel = new AccountRspModel(user,true);
            return ResponseModel.buildOk(accountRspModel);
        }
    }
}
