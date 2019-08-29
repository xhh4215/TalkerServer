package com.xiaohei.web.italker.push.service;

import com.xiaohei.web.italker.push.bean.api.account.AccountRspModel;
import com.xiaohei.web.italker.push.bean.api.account.RegisterModel;
import com.xiaohei.web.italker.push.bean.api.base.ResponseModel;
import com.xiaohei.web.italker.push.bean.card.UserCard;
import com.xiaohei.web.italker.push.bean.db.User;
import com.xiaohei.web.italker.push.factory.UserFactory;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author qiujuer
 */
// 127.0.0.1/api/account/...
@Path("/account")
public class AccountService {
    // 注册
    @POST
    @Path("/register")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> register(RegisterModel model) {
        User user = UserFactory.foundByPhone(model.getAccount().trim());
        if (user != null) {
            return ResponseModel.buildHaveAccountError();
        }
        user = UserFactory.foundByName(model.getName().trim());
        if (user != null) {
            return ResponseModel.buildHaveNameError();
        }
        user = UserFactory.rregister(model.getAccount(), model.getName(), model.getPassword());
        if (user != null) {
            AccountRspModel accountRspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(accountRspModel);

        } else {
            return ResponseModel.buildRegisterError();
        }
    }
}
