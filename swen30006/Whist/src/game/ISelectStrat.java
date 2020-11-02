package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public interface ISelectStrat {
    public Card select(ArrayList<Card> hand);
}
