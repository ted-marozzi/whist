package game;

import ch.aplu.jcardgame.Hand;

public interface IFilterStrat {

    Hand getFilteredHand(Player player);
}
