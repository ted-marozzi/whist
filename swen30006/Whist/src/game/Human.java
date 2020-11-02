
package game;

import ch.aplu.jcardgame.Card;
import static ch.aplu.jgamegrid.GameGrid.delay;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardListener;
import ch.aplu.jcardgame.Hand;

public class Human extends Player {

    private Card chosenCard;


    public Human(int playerID, Hand hand) {
        super(playerID, hand);
        // TODO Auto-generated constructor stub
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) { chosenCard = card; hand.setTouchEnabled(false); }
        };
        hand.addCardListener(cardListener);

    }

    @Override
    public Card chooseCard() {
        chosenCard = null;
        getHand().setTouchEnabled(true);
        while (null == chosenCard) {
            delay(100);

        }
        return chosenCard;
    }

    public String getStatusText(String leadOrFollow) {
        return "Player "+ getPlayerID() +" double-click on card to " + leadOrFollow + ".";
    }



}