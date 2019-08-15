package com.xiaohei.web.italker.push.service;


import com.xiaohei.web.italker.push.bean.api.account.RegisterMode;
import com.xiaohei.web.italker.push.bean.db.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

//  127.0.0.1/api/account/....
@Path("/account")
public class AccountService {
    @POST
    @Path("/register")
    //指定请求和响应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RegisterMode register(RegisterMode registerMode) {
        return registerMode;
////        User user = new User();
////        user.setName(registerMode.getName());
////        user.setSex(2);
////        return user;
    }
}
