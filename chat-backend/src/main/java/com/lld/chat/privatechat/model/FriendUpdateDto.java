package com.lld.chat.privatechat.model;

public class FriendUpdateDto {
    private String friend;
    private int unreadCount;

    public FriendUpdateDto() {}

    public FriendUpdateDto(String friend, int unreadCount) {
        this.friend = friend;
        this.unreadCount = unreadCount;
    }

    public String getFriend() { return friend; }
    public void setFriend(String friend) { this.friend = friend; }
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
