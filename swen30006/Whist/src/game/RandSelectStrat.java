package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandSelectStrat implements ISelectStrat {
    static final Random random = ThreadLocalRandom.current();
    // return random Card from ArrayList
    
    @Override
    public Card select(ArrayList<Card> hand) {
        int x = random.nextInt(hand.size());
        return hand.get(x);
    }
}
