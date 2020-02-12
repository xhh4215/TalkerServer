package com.xiaohei.web.italker.push.factory;

import com.google.common.base.Strings;
import com.xiaohei.web.italker.push.bean.db.User;
import com.xiaohei.web.italker.push.bean.db.UserFollow;
import com.xiaohei.web.italker.push.utils.Hib;
import com.xiaohei.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class UserFactory {
    public static User register(String account, String name, String password) {
        account = account.trim();
        password = encodePassword(password);
        User user = createUser(account,password,name);
         if (user!=null){
             user = login(user);
         }
         return  user;
    }
    public static  User Login(String account,String password){
        final String accountStr = account.trim();
        final String encodepassword = encodePassword(password);
        User user = Hib.query(session -> (User) session.createQuery("from User where phone = :phone and password = :password")
                  .setParameter("phone",accountStr)
                  .setParameter("password",encodepassword)
                  .uniqueResult());
        if(user!=null){
            user = login(user);

        }
        return user;

    }
    private static  User login(User user){
        String newToken = UUID.randomUUID().toString();
        newToken = TextUtil.encodeBase64(newToken);
        user.setToken(newToken);
        return  update(user);
    }
    private static  User createUser(String account,String password,String name){
       User user = new User();
       user.setName(name);
       user.setPassword(password);
       user.setPhone(account);
       return  Hib.query(session ->{
          session.save(user);
          return user;
       }
      );
   }
    public static User bindPushId(User user,String pushId){
        if (Strings.isNullOrEmpty(pushId)){
            return  null;
        }
        Hib.queryOnly(session ->{
            @SuppressWarnings("unchecked")
            List<User> list =  (List<User>) session.createQuery("from User where lower(pushId) = :pushId and id!= :userId")
                 .setParameter("pushId",pushId.toLowerCase())
                 .setParameter("userId",user.getId())
                 .list();
           for (User u:list){
               u.setPushId(null);
               session.saveOrUpdate(u);
           }
     });
        if (pushId.equalsIgnoreCase(user.getPushId())){
            return  user;
        }else{
            if (Strings.isNullOrEmpty(user.getPushId())){
                // todo 推送一个推出的消息
            }
            user.setPushId(pushId);
          return update(user);
        }
 }
    private static String encodePassword(String password) {
        password = password.trim();
        password = TextUtil.getMD5(password);
        return TextUtil.encodeBase64(password);
    }
    public static User foundByPhone(String phone) {
        return Hib.query(session -> {
            User user = (User) session.createQuery("from User where phone = :inPhone")
                    .setParameter("inPhone", phone).uniqueResult();
            return user;
        });
    }
    public static User foundByToken(String token) {
        return Hib.query(session -> {
            User user = (User) session.createQuery("from User where token = :token")
                    .setParameter("token", token).uniqueResult();
            return user;
        });
    }
    public static User foundByName(String name) {
        return Hib.query(session -> {
            User user = (User) session.createQuery("from User where name = :name")
                    .setParameter("name", name).uniqueResult();
            return user;
        });
    }
    public static User foundById(String id) {
        return Hib.query(session ->  session.get(User.class,id));
    }
    public static User update(User user){
       return  Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }

    /***
     * 获取我的联系人的列表
     * @param self user
     * @return
     */
    public static List<User>  constacts(User self){
         return Hib.query(session -> {
             session.load(self,self.getId());
             Set<UserFollow>  follows= self.getFollowing();
             return follows.stream()
                     .map(UserFollow::getTarget)
                     .collect(Collectors.toList());
         });
    }

    /****
     * 关注一个人的操作
     * @param origin 发起者
     * @param target 被关注者
     * @param alias  备注名
     * @return 被关注人的信息
     */
    public static User follow(final User origin,final User target,final String alias){
      UserFollow follow= getUserFollow(origin,target);
      if (follow!=null){
          return follow.getTarget();
      }

       return  Hib.query(session -> {
           session.load(origin,origin.getId());
           session.load(target,target.getId());
           UserFollow originFollow = new UserFollow();
           originFollow.setOrigin(origin);
           originFollow.setTarget(target);
           originFollow.setAlias(alias);


           UserFollow targetFollow = new UserFollow();
           targetFollow.setOrigin(target);
           targetFollow.setTarget(origin);
           session.save(originFollow);
           session.save(targetFollow);
           return target;
       });


   }

    /***
     * 查询两个人是否已经关注
     * @param origin 发起者
     * @param target 被关注者
     * @return
     */
    public static UserFollow getUserFollow(final User origin,final User target){
      return Hib.query(session -> (UserFollow) session.createQuery("from UserFollow  where originId = :originId and targetId=:targetId")
              .setParameter("originId",origin.getId())
              .setParameter("targetId",target.getId())
              .setMaxResults(1)
              .uniqueResult());
   }


    public static List<User> search(String name) {
        if (Strings.isNullOrEmpty(name))
         name = ""; // 保证不能为null的情况，减少后面的一下判断和额外的错误
         final String searchName = "%" + name + "%"; // 模糊匹配
        return Hib.query(session -> {
        // 查询的条件：name忽略大小写，并且使用like（模糊）查询；
        // 头像和描述必须完善才能查询到
        return (List<User>) session.createQuery("from User where lower(name) like :name and portrait is not null and description is not null")
                .setParameter("name", searchName)
                .setMaxResults(20) // 至多20条
                .list();

    });

}
}
