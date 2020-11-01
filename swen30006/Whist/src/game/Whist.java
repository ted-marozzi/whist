package game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("serial")
public class Whist extends CardGame {

    /**********************************************************************************************
     * Random
     */
    static final Random random = ThreadLocalRandom.current();

    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }




    /**********************************************************************************************
     * Variables
     */
    private final String version = "1.0";
    public final int nbStartCards = 13;
    public final int winningScore = 24;
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private boolean enforceRules = false;
    private final int thinkingTime = 2000;
    private final Deck deck = new Deck(WhistCard.Suit.values(), WhistCard.Rank.values(), "cover");
    private Player winner = null;
    private Card winningCard = null;
    private WhistCard.Suit lead = null;

    /**********************************************************************************************
     * Locations and Graphics
     */
    /* Up and Right refer to the reference frame of the Player */
    private static final int WIDTH = 700, HEIGHT = WIDTH, HAND_PAD = 75, UP_PAD = 25, RIGHT_PAD = 125;
    private final Location[] handLocations = {
            new Location(WIDTH/2, HEIGHT- HAND_PAD),
            new Location(HAND_PAD, HEIGHT/2),
            new Location(WIDTH/2, HAND_PAD),
            new Location(WIDTH- HAND_PAD, HEIGHT/2)
    };

    private final Location[] scoreLocations = {
            new Location(WIDTH-RIGHT_PAD, HEIGHT-UP_PAD),
            new Location(UP_PAD, HEIGHT-RIGHT_PAD),
            new Location(RIGHT_PAD, UP_PAD),
            new Location(WIDTH-UP_PAD, RIGHT_PAD)
    };

    private Actor[] scoreActors = {null, null, null, null};
    private final Location trickLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private final Location hideLocation = new Location(-500, -500);
    private Location trumpsActorLocation = new Location(50, 50);
    Font bigFont = new Font("Serif", Font.BOLD, 36);

    /**********************************************************************************************
     * Players
     */
    private final List<Player> players = new ArrayList<>();
    public final int nbPlayers = 4;
    private Hand[] hands;
    private Card selected;


    private void initPlayers()  {
        players.add(new Human(0));
        for(int i = 1; i < nbPlayers; i++)  {
            players.add(new AI(i));
        }
    }

    private String printHand(ArrayList<Card> cards) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            out.append(cards.get(i).toString());
            if (i < cards.size() - 1) out.append(",");
        }
        return (out.toString());
    }

    private void initScore() {
        for (Player player:players) {
            player.setScore(0);
            scoreActors[player.getPlayerNum()] = new TextActor(Integer.toString(player.getScore()), Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[player.getPlayerNum()], scoreLocations[player.getPlayerNum()]);
        }
    }
    private void updateScore(Player player) {
        removeActor(scoreActors[player.getPlayerNum()]);
        scoreActors[player.getPlayerNum()] = new TextActor(String.valueOf(player.getScore()), Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player.getPlayerNum()], scoreLocations[player.getPlayerNum()]);
    }


    /**********************************************************************************************
     * Methods
     */

    public void setStatus(String string) {
        setStatusText(string);
    }


    private void initRound() {

        hands = deck.dealingOut(players.size(), nbStartCards); // Last element of hands is leftover cards; these are ignored
        for (int i = 0; i < players.size(); i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, true);
            players.get(i).setHand(hands[i]);
        }
        // Set up human player for interaction
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) {
                selected = card;
                players.get(0).getHand().setTouchEnabled(false);
            }
        };
        players.get(0).getHand().addCardListener(cardListener);
        // graphics
        RowLayout[] layouts = new RowLayout[players.size()];
        for (int i = 0; i < players.size(); i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            players.get(i).getHand().setView(this, layouts[i]);
            players.get(i).getHand().setTargetArea(new TargetArea(trickLocation));
            players.get(i).getHand().draw();
        }
    }

    private void selectCard(Player nextPlayer, Boolean isLead)  {



        String leadOrFollow;

        if(isLead)  {
            leadOrFollow = "lead.";
        } else  {
            leadOrFollow = "follow.";
        }


        if (nextPlayer instanceof Human) {  // Select lead depending on player type
            nextPlayer.getHand().setTouchEnabled(true);
            setStatus("Player " + nextPlayer.getPlayerNum() + " double-click on card to " + leadOrFollow);
            while (null == selected) delay(100);
        } else if(nextPlayer instanceof AI){
            setStatusText("Player " + nextPlayer.getPlayerNum() + " thinking...");
            delay(thinkingTime);
            // Player selection algorithm
            selected = ((AI) nextPlayer).select( new RandomSelect(), nextPlayer.getHand());
        }



    }


    private void checkSuit(Player nextPlayer)    {

        if (selected.getSuit() != lead && nextPlayer.getHand().getNumberOfCardsWithSuit(lead) > 0) {
            // Rule violation
            String violation = "Follow rule broken by player " + nextPlayer.getPlayerNum() + " attempting to play " + selected;
            //System.out.println(violation);
            if (enforceRules)
                try {
                    throw (new BrokeRuleException(violation));
                } catch (BrokeRuleException e) {
                    e.printStackTrace();
                    System.out.println("A cheating player spoiled the game!");
                    System.exit(0);
                }
        }

    }

    private void drawTrick(Hand trick) {

        // Lead with selected card
        trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
        trick.draw();
        selected.setVerso(false);

    }

    private Player getNextPlayer(Player lastPlayer)    {

        if (lastPlayer.getPlayerNum() + 1 >= nbPlayers) {
            return players.get(0);  // From last back to first
        } else {
            return players.get(lastPlayer.getPlayerNum() + 1);
        }
    }

    private Boolean currentWinner(Card winningCard, WhistCard.Suit trumps) {
        return (selected.getSuit() == winningCard.getSuit() && WhistCard.rankGreater(selected, winningCard)) ||
                // trumped when non-trump was winning
                (selected.getSuit() == trumps && winningCard.getSuit() != trumps);
    }


    private void lead(Player nextPlayer, Hand trick, WhistCard.Suit trumps) {
        selected = null;
        selectCard(nextPlayer, true);
        drawTrick(trick);

        // No restrictions on the card being lead
        lead = (WhistCard.Suit) selected.getSuit();
        selected.transfer(trick, true); // transfer to trick (includes graphic effect)

        winner = nextPlayer;
        winningCard = selected;

        System.out.println("New trick: Lead Player = " + nextPlayer.getPlayerNum() + ", Lead suit = " + selected.getSuit() + ", Trump suit = " + trumps);
        System.out.println("Player " + nextPlayer.getPlayerNum() + " play: " + selected.toString() + " from [" + printHand(players.get(nextPlayer.getPlayerNum()).getHand().getCardList()) + "]");
        // End Lead
    }

    private void follow(Player nextPlayer, Hand trick, WhistCard.Suit trumps)   {

        selected = null;
        selectCard(nextPlayer, false);
        // Follow with selected card
        drawTrick(trick);

        // Check: Following card must follow suit if possible
        checkSuit(nextPlayer);
        // End Check
        selected.transfer(trick, true); // transfer to trick (includes graphic effect)
        System.out.println("Winning card: " + winningCard.toString());
        System.out.println("Player " + nextPlayer.getPlayerNum() + " play: " + selected.toString() + " from [" + printHand(nextPlayer.getHand().getCardList()) + "]");
        if (currentWinner(winningCard, trumps) ) {
            winner = nextPlayer;
            winningCard = selected;
        }
        // End Follow

    }

    private void removeTrick(Hand trick) {

        delay(600);
        trick.setView(this, new RowLayout(hideLocation, 0));
        trick.draw();

    }

    private Optional<Integer> playRound() {

        // Returns winner, if any
        // Select and display trump suit
        final WhistCard.Suit trumps = randomEnum(WhistCard.Suit.class);
        final Actor trumpsActor = new Actor("sprites/" + WhistCard.trumpImage[trumps.ordinal()]);
        addActor(trumpsActor, trumpsActorLocation);
        // End trump suit
        Hand trick;


        Player nextPlayer = players.get(random.nextInt(players.size())); // randomly select player to lead for this round


        for (int i = 0; i < nbStartCards; i++) {
            trick = new Hand(deck);

            lead(nextPlayer, trick, trumps);

            for (int j = 1; j < nbPlayers; j++) {
                nextPlayer = getNextPlayer(nextPlayer);
                follow(nextPlayer, trick, trumps);
            }


            removeTrick(trick);

            nextPlayer = winner;
            System.out.println("Winner: " + winner.getPlayerNum());
            setStatusText("Player " + nextPlayer + " wins trick.");

            nextPlayer.setScore(nextPlayer.getScore() + 1);
            updateScore(nextPlayer);

            if (nextPlayer.getScore()==winningScore) return Optional.of(nextPlayer.getPlayerNum());
        }
        removeActor(trumpsActor);
        return Optional.empty();
    }

    public Whist() {
        super(700, 700, 30);
        setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initPlayers();
        initScore();
        Optional<Integer> winner;
        do {
            initRound();
            winner = playRound();
        } while (winner.isEmpty());
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText("Game over. Winner is player: " + winner.get());
        refresh();
    }

    public static void main(String[] args) {
        new Whist();
    }

}
