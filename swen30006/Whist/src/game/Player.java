/**
 * Code by by Jake Hum, Edward Marozzi, Fei Yuan - Team 1
 */

package game;
import ch.aplu.jcardgame.*;

/**
 * The player class implemented by both the human and ai classes
 */
public abstract class Player {
    protected Hand hand;
    private int score;

    private final int playerID;

    /**
     * @param playerID The player number
     */
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

    /**
     * @param isLead is the player leading
     * @param winningCard The current winning card
     * @return The chosen card.
     */
    public abstract Card chooseCard(boolean isLead, Card winningCard);

    public abstract String getStatusText(String leadOrFollow);

}