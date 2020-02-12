package com.xiaohei.web.italker.push.bean.card;

import com.google.gson.annotations.Expose;
import com.xiaohei.web.italker.push.bean.db.User;

import java.time.LocalDateTime;

public class UserCard {
    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String phone;
    @Expose
    private String portrait;
    @Expose
    private String desc;
    @Expose
    private int sex = 0;
    @Expose
    private LocalDateTime modifyAt;
    @Expose
    private int follows;
    @Expose
    private boolean isFollow;
    @Expose
    private int following;

    public UserCard(final User user) {
        this(user,false);

    }

    public UserCard(final User user,boolean isFollow) {
        this.isFollow = isFollow;
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.portrait = user.getPortrait();
        this.desc = user.getDescription();
        this.sex = user.getSex();
        this.modifyAt = user.getUpdateAt();

        // TODO 得到关注人和粉丝的数量
        // user.getFollowers().size()
        // 懒加载会报错，因为没有Session

    }


    public String getId() {
        return id;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public LocalDateTime getModifyAt() {
        return modifyAt;
    }

    public void setModfyAt(LocalDateTime modifyAt) {
        this.modifyAt = modifyAt;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }



    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }


}
