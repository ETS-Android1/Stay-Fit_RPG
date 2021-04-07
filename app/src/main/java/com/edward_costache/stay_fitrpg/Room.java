package com.edward_costache.stay_fitrpg;

public class Room {
    private String roomName, userID1, userID2;
    private int amountPlayers;

    Room(String roomName, String userID1)
    {
        setRoomName(roomName);
        setUserID1(userID1);
        setUserID2("");
        amountPlayers = 1;
    }

    Room()
    {

    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getUserID1() {
        return userID1;
    }

    public void setUserID1(String userID1) {
        this.userID1 = userID1;
    }

    public String getUserID2() {
        return userID2;
    }

    public void setUserID2(String userID2) {
        this.userID2 = userID2;
    }

    public int getAmountPlayers() {
        return amountPlayers;
    }

    public void setAmountPlayers(int amountPlayers) {
        this.amountPlayers = amountPlayers;
    }
}
