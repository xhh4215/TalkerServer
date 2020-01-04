package com.xiaohei.web.italker.push.service;

import com.xiaohei.web.italker.push.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class BaseService {
    @Context
    protected SecurityContext securityContext;
    protected User getSelf(){
        return (User) securityContext.getUserPrincipal();
    }
}
