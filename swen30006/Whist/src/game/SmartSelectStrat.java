package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class SmartSelectStrat implements ISelectStrat {

    @Override
    public Card select(Hand hand, Card winningCard) {


        Hand trumpSuit = hand.extractCardsWithSuit(Whist.trumps);
        Hand winningSuit = hand.extractCardsWithSuit(winningCard.getSuit());
        Card toPlay = null;
        // Check if can be winning by winning suit
        if(!winningSuit.isEmpty())    {
            Card bestWinning = highestRank(winningSuit);
            if(Whist.rankGreater(bestWinning, winningCard))   {
                toPlay =  bestWinning;
            }
        }
        // Check if can win by lowest trumps
        if(!trumpSuit.isEmpty() && Whist.trumps != winningCard.getSuit() ) {
            toPlay = lowestRank(trumpSuit);
        } else if(!trumpSuit.isEmpty()) {
            // Check if can win by highest trumps
            Card bestTrumps = highestRank(trumpSuit);
            if (Whist.rankGreater(bestTrumps, winningCard)) {
                toPlay = bestTrumps;
            } else {
                // If not play the lowest rank card possible
                toPlay = lowestRank(hand);
            }
        }
        return toPlay;
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
