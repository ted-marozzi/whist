package game;

import static ch.aplu.jgamegrid.GameGrid.delay;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class AI extends Player {
    private final int thinkingTime;
    private final IFilterStrat filterStrat;
    private final ISelectStrat selectStrat;

    public AI(int playerID, int thinkingTime, String playerProperty) {
        super(playerID);

        String filterProperty = playerProperty.split(":")[0];
        String selectProperty = playerProperty.split(":")[1];
        filterStrat = AIStratFactory.getInstance().getFilterStrat(filterProperty);
        selectStrat = AIStratFactory.getInstance().getSelectStrat(selectProperty);

        this.thinkingTime = thinkingTime;
    }

    @Override
    public Card chooseCard(boolean isLead, Card winningCard) {
        delay(thinkingTime);
        Hand filteredHand;
        // don't perform filtering if leading
        if (isLead) {
            filteredHand = getHand();
        } else {
            filteredHand = filterStrat.getFilteredHand(getHand());
        }
        Card selected = selectStrat.select(filteredHand, winningCard);
        return getHand().getCard(selected.getSuit(), selected.getRank());
    }

    public String getStatusText(String leadOrFollow) {
        return "Player " + getPlayerID() + " thinking...";
    }
}