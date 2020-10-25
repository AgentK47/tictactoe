package com.agentk.tictactoe;

/**
 * Created by AgentK on 03/01/2018.
 */

public class Score {

    int id;

    String won;
    String draw;
    String lost;
    public Score(){}
    public Score(int id, String won, String draw, String lost){
        this.id=id;
        this.won=won;
        this.draw=draw;
        this.lost=lost;
    }

    // getting ID
    public int getID(){
        return this.id;
    }

    // setting id
    public void setID(int id){
        this.id = id;
    }

    // getting won
    public String getWon(){
        return this.won;
    }

    // setting won
    public void setWon(String won){
        this.won = won;
    }

    // getting lost
    public String getLost(){
        return this.lost;
    }

    // setting lost
    public void setLost(String lost){
        this.lost = lost;
    }

    // getting draw
    public String getDraw(){
        return this.draw;
    }

    // setting name
    public void setDraw(String draw){
        this.draw = draw;
    }
}
