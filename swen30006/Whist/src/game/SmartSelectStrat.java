/**
 * Code by by Jake Hum, Edward Marozzi, Fei Yuan - Team 1
 */
package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

/**
 * Smart selection strategy, try's to play a lead card if will be winning
 *  if not try's to play a trump card if will be winning, if not plays
 *  the lowest rank card.
 */
public class SmartSelectStrat implements ISelectStrat {
    /**
     * @param hand hand to select card from
     * @param winningCard the current winning card
     * @return the card to be selected
     */
    @Override
    public Card select(Hand hand, Card winningCard) {

        // If no current winning card, return highest ranking card
        if (winningCard == null) {
            return highestRank(hand);
        }

        // Get all the trumps suit and lead suit cards
        Hand trumpSuit = hand.extractCardsWithSuit(Whist.trumps);
        Hand winningSuit = hand.extractCardsWithSuit(winningCard.getSuit());
        Card selectedCard = null;

        // Check if can win by current winning suit or lowest trumps
        if (winningCard.getSuit() != Whist.trumps) {

            if (!winningSuit.isEmpty()) {

                Card bestWinning = highestRank(winningSuit);
                // If can out rank the winning card play it
                if (Whist.rankGreater(bestWinning, winningCard)) {
                    selectedCard = bestWinning;
                } else if (!trumpSuit.isEmpty()) {
                    selectedCard = lowestRank(trumpSuit);
                }
                // If can win by playing a trump card play it
            } else if (!trumpSuit.isEmpty()) {
                selectedCard = lowestRank(trumpSuit);
            }
        }
        // Check if can win by highest trumps
        else if (!trumpSuit.isEmpty()) {
            Card bestTrumps = highestRank(trumpSuit);
            if (Whist.rankGreater(bestTrumps, winningCard)) {
                selectedCard = bestTrumps;
            }
        }
        // If no conditions were met, select the worst card
        if (selectedCard == null) {
            selectedCard = lowestRank(hand);
        }

        // return the selected card
        return selectedCard;
    }

    private Card highestRank(Hand hand) {
        hand.sort(Hand.SortType.RANKPRIORITY, false);
        return hand.getFirst();
    }

    private Card lowestRank(Hand hand) {
        hand.sort(Hand.SortType.RANKPRIORITY, false);
        return hand.getLast();
    }
}
