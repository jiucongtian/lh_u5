package com.longhuapuxin.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author zh
 * @date 2015-8-28
 */
@DatabaseTable(tableName = "user")
public class User {

    @DatabaseField(columnName = "user_id", id = true, canBeNull = false)
    private int userId;

    @DatabaseField(columnName = "channel_id")
    private int channelId;

    @DatabaseField(columnName = "nick")
    private String nick;

    @DatabaseField(columnName = "head_icon")
    private int headIcon;

    @DatabaseField(columnName = "group")
    private int group;

    public User() {
    }

    public User(int userId, int channelId, String nick, int headIcon, int group) {
        this.userId = userId;
        this.channelId = channelId;
        this.nick = nick;
        this.headIcon = headIcon;
        this.group = group;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", channelId=" + channelId
                + ", nick=" + nick + ", headIcon=" + headIcon + ", group="
                + group + "]";
    }

}
