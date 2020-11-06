package game;

import ch.aplu.jcardgame.Hand;

public class NaiveFilterStrat implements IFilterStrat {
    @Override
    public Hand getFilteredHand(Hand hand) {
        Hand trumpsCards = hand.extractCardsWithSuit(Whist.trumps);
        Hand leadCards = hand.extractCardsWithSuit(Whist.lead);

        Hand filteredHand = trumpsCards;
        if (Whist.trumps != Whist.lead)  {
            filteredHand.insert(leadCards, false);
        }
        if (filteredHand.getNumberOfCards() == 0) {
            filteredHand = hand;
        }

        return filteredHand;
    }
}
