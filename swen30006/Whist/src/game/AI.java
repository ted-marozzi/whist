package game;

import java.util.ArrayList;

import static ch.aplu.jgamegrid.GameGrid.delay;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class AI extends Player{

    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list) {
        int x = Whist.random.nextInt(list.size());
        return list.get(x);
    }
    
    private int thinkingTime;
    private IFilterStrat filterStrat;
    private ISelectStrat selectStrat;

    public AI(int playerID, int thinkingTime) {
        super(playerID);
        try {
            filterStrat = AIStratFactory.getInstance().getFilterStrat();
            selectStrat = AIStratFactory.getInstance().getSelectStrat();
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.thinkingTime = thinkingTime;
    }

    @Override
    public Card chooseCard(boolean isLead, Card winningCard) {
        delay(thinkingTime);
        Hand filteredHand;
        // don't perform filtering if leading
        if (isLead) {
            filteredHand = this.hand;
        } else {
            filteredHand = filterStrat.getFilteredHand(this.hand);
        }
        Card chosenCard = selectStrat.select(filteredHand, winningCard);
        return this.getHand().getCard(chosenCard.getSuit(), chosenCard.getRank());
    }

    public String getStatusText(String leadOrFollow) {
        return "Player " + getPlayerID() + " thinking...";
    }
}