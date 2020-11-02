package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandSelectStrat implements ISelectStrat {
    // return random Card from ArrayList
    
    @Override
    public Card select(Hand hand) {
        int x = Whist.random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }
}
