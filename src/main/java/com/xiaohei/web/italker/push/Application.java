package com.xiaohei.web.italker.push;
import com.xiaohei.web.italker.push.provider.GsonProvider;
import com.xiaohei.web.italker.push.service.AccountService;
import com.xiaohei.web.italker.push.service.AuthRequestFilter;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

public class Application extends ResourceConfig {
   public Application(){
       //注册逻辑处理
       packages(AccountService.class.getPackage().getName());
       register(AuthRequestFilter.class);
       //注册Json转化器
       //register(JacksonJsonProvider.class);
       register(GsonProvider.class);
       //注册日志打印
       register(Logger.class);
   }
}
