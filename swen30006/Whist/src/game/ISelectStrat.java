package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public interface ISelectStrat {
    Card select(Hand hand, Card winningCard);
}