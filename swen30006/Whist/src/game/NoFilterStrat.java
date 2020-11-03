package game;

import ch.aplu.jcardgame.Hand;

public class NoFilterStrat implements IFilterStrat {
    @Override
    public Hand getFilteredHand(Hand hand) {
        return hand;
    }
}