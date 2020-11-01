package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public interface SelectionStrategy {
    public Card select(Hand hand);


}
