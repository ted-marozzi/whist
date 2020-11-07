package game;

import ch.aplu.jcardgame.Hand;

/**
 * The naive way of filtering, removes illegal cards
 */
public class NaiveFilterStrat implements IFilterStrat {
    /**
     * @param hand The hand to be filtered
     * @return The filtered hand
     */
    @Override
    public Hand getFilteredHand(Hand hand) {
        Hand trumpsCards = hand.extractCardsWithSuit(Whist.trumps);
        Hand leadCards = hand.extractCardsWithSuit(Whist.lead);

        // Inserts trumps cards
        Hand filteredHand = trumpsCards;

        // Insert leads cards if they are different from trumps suit
        if (Whist.trumps != Whist.lead)  {
            filteredHand.insert(leadCards, false);
        }

        // If lead or trump suit cards then no filtering
        if (filteredHand.getNumberOfCards() == 0) {
            filteredHand = hand;
        }

        return filteredHand;
    }
}
