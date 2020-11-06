package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class SmartSelectStrat implements ISelectStrat {
    @Override
    public Card select(Hand hand, Card winningCard) {
        if (winningCard == null) {
            return highestRank(hand);
        }

        Hand trumpSuit = hand.extractCardsWithSuit(Whist.trumps);
        Hand winningSuit = hand.extractCardsWithSuit(winningCard.getSuit());
        Card selectedCard = null;

        // Check if can win by current winning suit or lowest trumps
        if (winningCard.getSuit() != Whist.trumps) {
            if (!winningSuit.isEmpty()) {
                Card bestWinning = highestRank(winningSuit);
                if (Whist.rankGreater(bestWinning, winningCard)) {
                    selectedCard = bestWinning;
                } else if (!trumpSuit.isEmpty()) {
                    selectedCard = lowestRank(trumpSuit);
                }
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

        return hand.getCard(selectedCard.getSuit(), selectedCard.getRank());
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
