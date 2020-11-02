package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class NaiveFilterStrat implements IFilterStrat {



        @Override
        public ArrayList<Card> getFilteredHand(Player player) {
            ArrayList<Card> hand = player.hand.getCardList();
            ArrayList<Card> filteredHand = (ArrayList<Card>) hand.stream()
                    .filter(x -> x.getSuit() == Whist.trumps || x.getSuit() == Whist.lead)
                    .collect(Collectors.toList());
            if (filteredHand.size() == 0) {
                filteredHand = hand;
            }
            return filteredHand;
        }




}
