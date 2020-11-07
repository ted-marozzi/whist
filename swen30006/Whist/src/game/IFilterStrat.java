/**
 * Code by by Jake Hum, Edward Marozzi, Fei Yuan - Team 1
 */
package game;

import ch.aplu.jcardgame.Hand;

/**
 * The interface for filtering strategies
 */
public interface IFilterStrat {
    Hand getFilteredHand(Hand hand);
}
