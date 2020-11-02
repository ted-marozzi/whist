package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

public class HighestRankSelectStrat implements ISelectStrat {

    @Override
    public Card select(Hand hand) {

        hand.sort(Hand.SortType.RANKPRIORITY, true);
        return hand.get(0);
    }
}
