/**
 * Code by by Jake Hum, Edward Marozzi, Fei Yuan - Team 1
 */
package game;

import ch.aplu.jcardgame.Hand;

/**
 * Trump filtering strategy, extracts card of the trump suit.
 */
public class TrumpFilterStrat implements IFilterStrat {
    /**
     * @param hand The hand to be filtered
     * @return The filtered hand.
     */
    @Override
    public Hand getFilteredHand(Hand hand) {
        Hand filteredHand = hand.extractCardsWithSuit(Whist.lead);

        if (filteredHand.getNumberOfCards() == 0) {
            filteredHand = hand.extractCardsWithSuit(Whist.trumps);
        }
        if (filteredHand.getNumberOfCards() == 0) {
            filteredHand = hand;
        }

        return filteredHand;
    }
}