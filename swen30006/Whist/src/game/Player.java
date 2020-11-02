package game;
import ch.aplu.jcardgame.*;

public abstract class Player {
    protected Hand hand;
    private int score;

    private final int playerID;

    public Player(int playerID) {
        this.hand = null;
        this.playerID = playerID;
        this.score = 0;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getScore() {
        return score;
    }

    public void increaseScore() {
        score++;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public abstract Card chooseCard(boolean isLead, Card winningCard);

    public abstract String getStatusText(String leadOrFollow);

}