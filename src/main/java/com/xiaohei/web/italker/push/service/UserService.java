package com.xiaohei.web.italker.push.service;

import com.google.common.base.Strings;
import com.xiaohei.web.italker.push.bean.api.account.AccountRspModel;
import com.xiaohei.web.italker.push.bean.api.base.ResponseModel;
import com.xiaohei.web.italker.push.bean.api.user.UpdateInfoModel;
import com.xiaohei.web.italker.push.bean.card.UserCard;
import com.xiaohei.web.italker.push.bean.db.User;
import com.xiaohei.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/***
 * 用户信息处理的Service
 */
// 127.0.0.1/api/user/...

@Path("/user")
public class UserService extends BaseService {

    @PUT
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update( UpdateInfoModel updateInfoModel){
        if(!UpdateInfoModel.check(updateInfoModel)){
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();

        self = updateInfoModel.updateToUser(self);
        self = UserFactory.update(self);
        UserCard card = new UserCard(self,true);
        return  ResponseModel.buildOk(card);

    }

}
