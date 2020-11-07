/**
 * Code by by Jake Hum, Edward Marozzi, Fei Yuan - Group 4
 */
package game;

import ch.aplu.jcardgame.Hand;

/**
 * Returns the hand it was given, no filtering
 */
public class NoFilterStrat implements IFilterStrat {
    @Override
    public Hand getFilteredHand(Hand hand) {
        return hand;
    }
}