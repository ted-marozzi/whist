package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class NaiveFilterStrat implements IFilterStrat {
    @Override
    public Hand getFilteredHand(Hand hand) {
        Hand filteredHand = null;

        Hand trumpsCards = hand.extractCardsWithSuit(Whist.trumps);
        Hand leadCards = hand.extractCardsWithSuit(Whist.lead);

        filteredHand = trumpsCards;

        if (Whist.trumps != Whist.lead)  {
            filteredHand.insert(leadCards, false);
        }

        if (filteredHand.getNumberOfCards() == 0) {
            filteredHand = hand;
        }
        return filteredHand;
    }
}
