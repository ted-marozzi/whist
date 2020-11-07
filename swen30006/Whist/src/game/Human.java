/**
 * Code by by Jake Hum, Edward Marozzi, Fei Yuan - Team 1
 */
package game;

import ch.aplu.jcardgame.Card;
import static ch.aplu.jgamegrid.GameGrid.delay;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardListener;
import ch.aplu.jcardgame.Hand;

/**
 * Human player class
 */
public class Human extends Player {

    private Card chosenCard;

    public Human(int playerID) {
        super(playerID);
    }

    /**
     * @param hand The hand of the human
     */
    @Override
    public void setHand(Hand hand) {
        this.hand = hand;
        // A listener to detect double clicks on the card
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) { chosenCard = card; hand.setTouchEnabled(false); }
        };
        hand.addCardListener(cardListener);
    }

    /**
     * @param isLead is the human taking the lead or follow
     * @param winningCard The current winning card
     * @return The card to be chosen
     */
    @Override
    public Card chooseCard(boolean isLead, Card winningCard) {
        chosenCard = null;
        getHand().setTouchEnabled(true);
        while (null == chosenCard) {
            delay(100);
        }
        return chosenCard;
    }

    /**
     * @param leadOrFollow Is the human leading or following
     * @return The formatted string to be displayed in the status bar
     */
    public String getStatusText(String leadOrFollow) {
        return "Player " + getPlayerID() + " double-click on card to " + leadOrFollow + ".";
    }

}