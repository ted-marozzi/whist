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

    public AI(int playerID, Hand hand, int thinkingTime) {
        super(playerID, hand);
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
    public Card chooseCard() {
        delay(thinkingTime);
        Hand filteredHand = filterStrat.getFilteredHand(this);
        Card choosenCard = selectStrat.select(filteredHand);
        return this.getHand().getCard(choosenCard.getSuit(), choosenCard.getRank());
    }

    public String getStatusText(String leadOrFollow) {
        return "Player " + getPlayerID() + " thinking...";
    }
}