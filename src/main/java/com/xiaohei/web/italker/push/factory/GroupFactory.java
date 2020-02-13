package com.xiaohei.web.italker.push.factory;

import com.xiaohei.web.italker.push.bean.db.Group;
import com.xiaohei.web.italker.push.bean.db.GroupMember;
import com.xiaohei.web.italker.push.bean.db.User;

import java.util.Set;

/***
 * 群数据库处理
 */
public class GroupFactory {
    /**
     * 通过groupid查找群  user 必须为群成员
     *
     * @param sender
     * @param groupId
     * @return
     */
    public static Group findById(User sender, String groupId) {
        return null;
    }
    public static Group findById(String groupId) {
        return null;
    }

    /**
     * 查询群成员
     * @param group
     * @return
     */
    public static Set<GroupMember> getMunbers(Group group) {
        return null;
    }
}
