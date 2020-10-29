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
     * Card Meta
     */


    public enum Suit {
        SPADES, HEARTS, DIAMONDS, CLUBS
    }

    public enum Rank {
        // Reverse order of rank importance (see rankGreater() below)
        // Order of cards is tied to card images
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
    }

    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    final String[] trumpImage = {"bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif"};


    /**********************************************************************************************
     * Random
     */
    static final Random random = ThreadLocalRandom.current();

    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // return random Card from Hand
    public static Card randomCard(Hand hand) {
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list) {
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    /**********************************************************************************************
     * Variables
     */
    private final String version = "1.0";
    public final int nbStartCards = 13;
    public final int winningScore = 24;
    private static final int handWidth = 400;
    private final int trickWidth = 40;
    private boolean enforceRules = false;
    private final int thinkingTime = 2000;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");

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
    private Location hideLocation = new Location(-500, -500);
    private Location trumpsActorLocation = new Location(50, 50);
    Font bigFont = new Font("Serif", Font.BOLD, 36);

    /**********************************************************************************************
     * Players
     */
    private List<Player> players = new ArrayList<>();
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
        hands = deck.dealingOut(nbPlayers, nbStartCards); // Last element of hands is leftover cards; these are ignored
        for (int i = 0; i < nbPlayers; i++) {
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
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            players.get(i).getHand().setView(this, layouts[i]);
            players.get(i).getHand().setTargetArea(new TargetArea(trickLocation));
            players.get(i).getHand().draw();
        }
    }


    private Optional<Integer> playRound() {  // Returns winner, if any
        // Select and display trump suit
        final Suit trumps = randomEnum(Suit.class);
        final Actor trumpsActor = new Actor("sprites/" + trumpImage[trumps.ordinal()]);
        addActor(trumpsActor, trumpsActorLocation);
        // End trump suit
        Hand trick;
        Player winner;
        Card winningCard;
        Suit lead;
        Player nextPlayer = players.get(random.nextInt(nbPlayers)); // randomly select player to lead for this round
        for (int i = 0; i < nbStartCards; i++) {
            trick = new Hand(deck);
            selected = null;
            if (nextPlayer.getPlayerNum() == 0) {  // Select lead depending on player type
                players.get(0).getHand().setTouchEnabled(true);
                setStatus("Player 0 double-click on card to lead.");
                while (null == selected) delay(100);
            } else {
                setStatusText("Player " + nextPlayer.getPlayerNum() + " thinking...");
                delay(thinkingTime);
                selected = randomCard(players.get(nextPlayer.getPlayerNum()).getHand());
            }
            // Lead with selected card
            trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
            trick.draw();
            selected.setVerso(false);
            // No restrictions on the card being lead
            lead = (Suit) selected.getSuit();
            selected.transfer(trick, true); // transfer to trick (includes graphic effect)
            winner = nextPlayer;
            winningCard = selected;
            System.out.println("New trick: Lead Player = " + nextPlayer.getPlayerNum() + ", Lead suit = " + selected.getSuit() + ", Trump suit = " + trumps);
            System.out.println("Player " + nextPlayer.getPlayerNum() + " play: " + selected.toString() + " from [" + printHand(players.get(nextPlayer.getPlayerNum()).getHand().getCardList()) + "]");
            // End Lead
            for (int j = 1; j < nbPlayers; j++) {
                if (nextPlayer.getPlayerNum() + 1 >= nbPlayers) {
                    nextPlayer = players.get(0);  // From last back to first
                } else {
                    nextPlayer = players.get(nextPlayer.getPlayerNum() + 1);
                }
                selected = null;

                if (nextPlayer.getPlayerNum() == 0) {
                    players.get(0).getHand().setTouchEnabled(true);
                    setStatus("Player 0 double-click on card to follow.");
                    while (null == selected) delay(100);
                } else {

                    setStatusText("Player " + nextPlayer.getPlayerNum() + " thinking...");
                    delay(thinkingTime);
                    selected = randomCard(nextPlayer.getHand());
                }
                // Follow with selected card
                trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
                trick.draw();
                selected.setVerso(false);  // In case it is upside down
                // Check: Following card must follow suit if possible
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
                // End Check
                selected.transfer(trick, true); // transfer to trick (includes graphic effect)
                System.out.println("Winning card: " + winningCard.toString());
                System.out.println("Player " + nextPlayer.getPlayerNum() + " play: " + selected.toString() + " from [" + printHand(nextPlayer.getHand().getCardList()) + "]");
                if ( // beat current winner with higher card
                        (selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
                                // trumped when non-trump was winning
                                (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
                    winner = nextPlayer;
                    winningCard = selected;
                }
                // End Follow
            }
            delay(600);
            trick.setView(this, new RowLayout(hideLocation, 0));
            trick.draw();
            nextPlayer = winner;
            System.out.println("Winner: " + winner.getPlayerNum());
            setStatusText("Player " + nextPlayer + " wins trick.");
            nextPlayer.setScore(nextPlayer.getScore() + 1);
            updateScore(nextPlayer);
            if (winningScore == nextPlayer.getScore()) return Optional.of(nextPlayer.getPlayerNum());
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
