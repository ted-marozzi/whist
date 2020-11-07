/**
 * Code by by Jake Hum, Edward Marozzi, Fei Yuan - Group 4
 */
package game;

import static ch.aplu.jgamegrid.GameGrid.delay;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
/**
 * This class represents the computer player and is responsible for selecting a card when it is there turn.
 */
public class AI extends Player {
    /* The time the ai takes to move */
    private final int thinkingTime;
    /* The filtering and selection strategies of the ai */
    private final IFilterStrat filterStrat;
    private final ISelectStrat selectStrat;


    /**
     * @param playerID The player number
     * @param thinkingTime Time to make move
     * @param playerProperty Contains the filter and selection strategies of
     *                       the ai.
     */
    public AI(int playerID, int thinkingTime, String playerProperty) {
        super(playerID);

        String filterProperty = playerProperty.split(":")[0];
        String selectProperty = playerProperty.split(":")[1];
        filterStrat = AIStratFactory.getInstance().getFilterStrat(filterProperty);
        selectStrat = AIStratFactory.getInstance().getSelectStrat(selectProperty);
        this.thinkingTime = thinkingTime;
    }


    /**
     * @param isLead Is the player leading
     * @param winningCard The current Winning Card
     * @return The card to be played
     */
    @Override
    public Card chooseCard(boolean isLead, Card winningCard) {
        delay(thinkingTime);
        Hand filteredHand;
        // don't perform filtering if leading
        if (isLead) {
            filteredHand = getHand();
        } else {
            filteredHand = filterStrat.getFilteredHand(getHand());
        }
        // Select a card
        Card selected = selectStrat.select(filteredHand, winningCard);
        // Match that to the actual object for animation purposes.
        return getHand().getCard(selected.getSuit(), selected.getRank());
    }

    /**
     * @param leadOrFollow If the ai is leading or following, used in
     *                     the human class.
     * @return The text to be displayed
     */
    public String getStatusText(String leadOrFollow) {
        return "Player " + getPlayerID() + " thinking...";
    }
}