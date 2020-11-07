/**
 * Code by by Jake Hum, Edward Marozzi, Fei Yuan - Group 4
 */
package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

/**
 * The interface for selection strategies
 */
public interface ISelectStrat {
    Card select(Hand hand, Card winningCard);
}