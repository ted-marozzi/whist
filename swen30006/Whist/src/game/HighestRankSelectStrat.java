package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class HighestRankSelectStrat implements ISelectStrat {
    @Override
    public Card select(Hand hand, Card winningCard) {
        hand.sort(Hand.SortType.RANKPRIORITY, true);
        return hand.get(0);
    }
}
