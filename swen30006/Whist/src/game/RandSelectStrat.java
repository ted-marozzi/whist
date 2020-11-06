package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class RandSelectStrat implements ISelectStrat {
    // return random card from hand
    @Override
    public Card select(Hand hand, Card winningCard) {
        int x = Whist.random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }
}
