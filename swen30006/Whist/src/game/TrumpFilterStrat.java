package game;

import ch.aplu.jcardgame.Hand;

public class TrumpFilterStrat implements IFilterStrat {
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