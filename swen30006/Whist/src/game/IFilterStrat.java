package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public interface IFilterStrat {
    ArrayList<Card> getFilteredHand(Player player);
}
