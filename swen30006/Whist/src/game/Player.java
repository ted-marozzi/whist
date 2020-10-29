package game;

import ch.aplu.jcardgame.Hand;

public abstract class Player {

    private Hand hand;
    private int score, playerNum;


    public Player(int playerNum)    {

        this.playerNum = playerNum;

    }

    public void setScore(int score) {
        this.score = score;
    }


    public int getScore() {
        return score;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() {
        return hand;
    }

    public int getPlayerNum() {
        return playerNum;
    }
}
