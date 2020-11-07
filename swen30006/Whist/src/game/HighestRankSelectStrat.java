/**
 * Code by by Jake Hum, Edward Marozzi, Fei Yuan - Team 1
 */
package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

/**
 * Card selection strategy that selects the highest rank card in the hand.
 */
public class HighestRankSelectStrat implements ISelectStrat {
    /**
     * @param hand Hand is a hand of cards
     * @param winningCard Is the current winning card, unused here
     * @return The card to be selected
     */
    @Override
    public Card select(Hand hand, Card winningCard) {
        hand.sort(Hand.SortType.RANKPRIORITY, false);
        return hand.get(0);
    }
}
