package com.agentk.tictactoe;

/**
 * Created by AgentK on 15/12/2017.
 */

public class Level {
    int won;
    int lost;
    int draw;

    public Level(int won,int draw, int lost) {
        this.won=won;
        this.draw=draw;
        this.lost=lost;
    }
}


