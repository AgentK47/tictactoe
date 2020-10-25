package com.agentk.tictactoe.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by AgentK on 12/02/2018.
 */
@IgnoreExtraProperties
public class User {
    private String name, pushId,grid,status;

    public User() {
    }

    public User(String name ,String status) {
        this.name = name;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrid() {
        return grid;
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
