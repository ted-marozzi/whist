package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class AI extends Player {

    public AI(int playerNum) {
        super(playerNum);
    }

    public Card select(SelectionStrategy strategy, Hand hand) {
        return strategy.select(hand);
    }


}
