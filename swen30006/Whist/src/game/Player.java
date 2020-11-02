package game;
import ch.aplu.jcardgame.*;

public abstract class Player {
    protected Hand hand;
    private int score;

    private int playerID;

    public Player(int playerID, Hand hand) {
        this.hand = hand;
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


    public abstract Card chooseCard();

    public abstract String getStatusText(String leadOrFollow);

}