package com.xiaohei.web.italker.push.service;
import com.google.common.base.Strings;
import com.xiaohei.web.italker.push.bean.api.base.PushModel;
import com.xiaohei.web.italker.push.bean.api.base.ResponseModel;
import com.xiaohei.web.italker.push.bean.api.user.UpdateInfoModel;
import com.xiaohei.web.italker.push.bean.card.UserCard;
import com.xiaohei.web.italker.push.bean.db.User;
 import com.xiaohei.web.italker.push.factory.UserFactory;
import com.xiaohei.web.italker.push.utils.PushDispatcher;

import javax.ws.rs.*;
 import javax.ws.rs.core.MediaType;
 import java.util.List;
import java.util.stream.Collectors;

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
    @GET
    @Path("/contact")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
   public  ResponseModel<List<UserCard>> contact(){
    User self = getSelf();
    List<User> users = UserFactory.constacts(self);
    List<UserCard> userCards = users.stream().map(user -> new UserCard(user,true)).collect(Collectors.toList());
    return ResponseModel.buildOk(userCards);
   }
    @PUT  //修改类用put
    @Path("/follow/{followId}")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
   public  ResponseModel<UserCard> follow(@PathParam("followId") String followId){
     User self = getSelf();
     if (self.getId().equalsIgnoreCase(followId)||Strings.isNullOrEmpty(followId)){
         return  ResponseModel.buildParameterError();
     }

     User followUser = UserFactory.foundById(followId);
     if (followUser==null){
         return ResponseModel.buildNotFoundUserError(null);
     }
         followUser = UserFactory.follow(self,followUser,null);
     if (followUser==null){
         return ResponseModel.buildServiceError();

     }
     // todo 通知我关注的人
     return ResponseModel.buildOk(new UserCard(followUser,true));
   }
    @GET  //修改类用put
    @Path("{id}")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
   public ResponseModel<UserCard> getUser(@PathParam("id") String id){
        if (Strings.isNullOrEmpty(id)){
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)){
            return ResponseModel.buildOk(new UserCard(self,true));
        }
        User user = UserFactory.foundById(id);
        if (user==null){
            return ResponseModel.buildNotFoundUserError(null);
        }
        boolean isFollow =UserFactory.getUserFollow(self,user)!=null;
        return ResponseModel.buildOk(new UserCard(user,isFollow));
   }

    @GET
    @Path("/search/{name:(.*)?}")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public  ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name){
        User self = getSelf();
        List<User> searchUsers = UserFactory.search(name);
        List<User> contacts = UserFactory.constacts(self);
        List<UserCard> userCards = searchUsers.stream()
                .map(user -> {
                    boolean isFollow = user.getId().equalsIgnoreCase(self.getId())
                            || contacts.stream().anyMatch(contractUser-> contractUser.getId().equalsIgnoreCase(user.getId()));
                    return new UserCard(user,isFollow);
                }).collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }
}
