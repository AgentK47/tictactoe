package com.agentk.tictactoe.model;

/**
 * Created by AgentK on 19/03/2018.
 */

public class RowItem {

    private int imageId;
    private String title;

    public RowItem(String title, int imageId){
    this.title=title;
    this.imageId=imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }


}
