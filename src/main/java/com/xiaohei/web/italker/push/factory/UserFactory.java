package com.xiaohei.web.italker.push.factory;

import com.xiaohei.web.italker.push.bean.db.User;
import com.xiaohei.web.italker.push.utils.Hib;
import com.xiaohei.web.italker.push.utils.TextUtil;
import org.hibernate.Session;


/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class UserFactory {
    public static User rregister(String account, String name, String password) {
        account = account.trim();
        password = encodePassword(password);
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);
        Session session = Hib.session();
        session.beginTransaction();
        try {
            session.save(user);
            session.getTransaction().commit();
            return user;
        } catch (Exception e) {
            session.getTransaction().rollback();
            return null;
        }

    }


    private static String encodePassword(String password) {
        password = password.trim();
        password = TextUtil.getMD5(password);
        return TextUtil.encodeBase64(password);
    }

    public static User foundByPhone(String phone) {
        return Hib.query(new Hib.Query<User>() {
            @Override
            public User query(Session session) {
                User user = (User) session.createQuery("from User where phone = :inPhone")
                        .setParameter("inPhone", phone).uniqueResult();
                return user;
            }
        });
    }
    public static User foundByName(String name) {
        return Hib.query(new Hib.Query<User>() {
            @Override
            public User query(Session session) {
                User user = (User) session.createQuery("from User where name = :name")
                        .setParameter("name", name).uniqueResult();
                return user;
            }
        });
    }

}
