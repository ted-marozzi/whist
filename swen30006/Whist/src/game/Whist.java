package game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Whist card game based on the jcard frame work extending CardGame
 */
@SuppressWarnings("serial")
public class Whist extends CardGame {

    private String string;

    /**********************************************************************************************
     * Card Meta
     */
    // Suits of the cards
    public enum Suit {
        SPADES, HEARTS, DIAMONDS, CLUBS
    }

    public enum Rank {
        // Reverse order of rank importance (see rankGreater() below)
        // Order of cards is tied to card images
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
    }

    public static boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    /**
     * Images names of the suits for trump suit image
     */
    public static final String[] trumpImage = {"bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif"};

    /**********************************************************************************************
     * Random
     */

    static Random random;

    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    /**********************************************************************************************
     * Variables
     */
    private final String version = "1.0";

    // The number of start cards
    private static int nbStartCards;
    // Score too win
    private static int winningScore;
    // Should the game prevent cheating
    private static boolean enforceRules;
    // Time to think
    private static int thinkingTime;

    // Create a deck based on enums
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private Player winner = null;
    private Card winningCard = null;
    protected static Suit lead;
    protected static Suit trumps;

    /**********************************************************************************************
     * Locations and Graphics
     */
    private final int handWidth = 400;
    private final int trickWidth = 40;

    /* Up and Right refer to the reference frame of the Player */
    private static int width, height;
    // Padding for the game
    private final int HAND_PAD = 75, UP_PAD = 25, RIGHT_PAD = 125;
    // Locations of hand
    private final Location[] handLocations = {
            new Location(width/2, height-HAND_PAD),
            new Location(HAND_PAD, height/2),
            new Location(width/2, HAND_PAD),
            new Location(width-HAND_PAD, height/2)
    };

    // Locations of score
    private final Location[] scoreLocations = {
            new Location(width-RIGHT_PAD, height-UP_PAD),
            new Location(UP_PAD, height-RIGHT_PAD),
            new Location(RIGHT_PAD, UP_PAD),
            new Location(width-UP_PAD, RIGHT_PAD)
    };

    private final Actor[] scoreActors = {null, null, null, null};
    private final Location trickLocation = new Location(width/2, height/2);
    private final Location textLocation = new Location(width/2, height/2+100);
    // Hide cards off screen
    private final Location hideLocation = new Location(-500, -500);
    private final Location trumpsActorLocation = new Location(50, 50);

    private static int fontSize;
    Font bigFont = new Font("Serif", Font.BOLD, fontSize);

    /**********************************************************************************************
     * Players
     */
    private static final List<String> playerProperties = new ArrayList<>();
    // Number of players 2-4
    private static int nbPlayers;
    private final List<Player> players = new ArrayList<>();
    private Card selected;

    // Initialise the players according to the config file
    private void initPlayers() {
        for (int i = 0; i < playerProperties.size(); i++) {
            String playerProperty = playerProperties.get(i);
            if (playerProperty.equals("human")) {
                players.add(new Human(i));
            } else {
                players.add(new AI(i, thinkingTime, playerProperty));
            }
        }
    }


    /**
     * @param cards List of cards
     * @return string to print
     */
    private String printHand(ArrayList<Card> cards) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            out.append(cards.get(i).toString());
            if (i < cards.size() - 1) out.append(",");
        }
        return (out.toString());
    }

    /**
     * Initilaise player scores to be displayed
     */
    private void initScore() {
        for (Player player : players) {
            scoreActors[player.getPlayerID()] = new TextActor(Integer.toString(player.getScore()), Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[player.getPlayerID()], scoreLocations[player.getPlayerID()]);
        }
    }

    /**
     * @param player The players score to be visually updated
     */
    private void updateScore(Player player) {
        removeActor(scoreActors[player.getPlayerID()]);
        scoreActors[player.getPlayerID()] = new TextActor(String.valueOf(player.getScore()), Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player.getPlayerID()], scoreLocations[player.getPlayerID()]);
    }

    /**********************************************************************************************
     * Methods
     */
    public void setStatus(String string) {
        this.string = string;
        setStatusText(string);
    }

    /**
     * Perform the first round set up
     */
    private void initRound() {
        // deal out cards with random seed
        Hand pack = deck.toHand(false);

        // Give the hands to players
        List<Hand> hands = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            hands.add(new Hand(deck));
        }
        // Deal starting cards to player
        for (int i = 0; i < nbStartCards; i++) {
            for (int j = 0; j < players.size(); j++) {
                int x = random.nextInt(pack.getNumberOfCards());
                Card dealt = pack.get(x);
                dealt.removeFromHand(false);
                hands.get(j).insert(dealt, false);
            }
        }
        // Sort the hands by suit
        for (int i = 0; i < players.size(); i++) {
            hands.get(i).sort(Hand.SortType.SUITPRIORITY, true);
            players.get(i).setHand(hands.get(i));
        }

        // Graphical layout positioning for the hand.
        RowLayout[] layouts = new RowLayout[players.size()];
        for (int i = 0; i < players.size(); i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            players.get(i).getHand().setView(this, layouts[i]);
            players.get(i).getHand().setTargetArea(new TargetArea(trickLocation));
            players.get(i).getHand().draw();
        }
    }

    /**
     * @param nextPlayer Check for rule violations of a player
     */
    private void checkSuit(Player nextPlayer) {
        // Ensure that if a player has a trumps suit or lead suit card that they played it
        if (selected.getSuit() != lead && selected.getSuit() != trumps &&
                (nextPlayer.getHand().getNumberOfCardsWithSuit(lead) > 0
                || nextPlayer.getHand().getNumberOfCardsWithSuit(trumps) > 0)) {
            // Rule violation
            String violation = "Follow rule broken by player " + nextPlayer.getPlayerID() + " attempting to play " + selected;

            if (enforceRules) {
                try {
                    throw (new BrokeRuleException(violation));
                } catch (BrokeRuleException e) {
                    e.printStackTrace();
                    System.out.println("A cheating player spoiled the game!");
                    System.exit(0);
                }
            }
        }
    }


    /**
     * @param trick The trick to be drawn in the middle of the board
     */
    private void drawTrick(Hand trick) {
        // Lead with selected card
        trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
        trick.draw();
        selected.setVerso(false);
    }

    /**
     * @param lastPlayer The previous player
     * @return Given the previous player determine the next player.
     */
    private Player getNextPlayer(Player lastPlayer)    {
        if (lastPlayer.getPlayerID() + 1 >= nbPlayers) {
            return players.get(0);  // From last back to first
        } else {
            return players.get(lastPlayer.getPlayerID() + 1);
        }
    }

    /**
     * @param winningCard The current winning card
     * @param trumps The trumps suit
     * @return A bool determining if the selected card is the new winning card or not.
     */
    private Boolean currentWinner(Card winningCard, Suit trumps) {
        return (selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
                // trumped when non-trump was winning
                (selected.getSuit() == trumps && winningCard.getSuit() != trumps);
    }

    /**
     * @param nextPlayer The player about to take the lead
     * @param trick The empty trick
     * @param trumps The trumps suit
     */
    private void lead(Player nextPlayer, Hand trick, Suit trumps) {
        setStatus(nextPlayer.getStatusText("lead"));
        selected = nextPlayer.chooseCard(true, winningCard);
        drawTrick(trick);

        // No restrictions on the card being lead
        lead = (Suit) selected.getSuit();
        selected.transfer(trick, true); // transfer to trick (includes graphic effect)

        winner = nextPlayer;
        winningCard = selected;

        System.out.println("New trick: Lead Player = " + nextPlayer.getPlayerID() + ", Lead suit = " + selected.getSuit() + ", Trump suit = " + trumps);
        System.out.println("Player " + nextPlayer.getPlayerID() + " play: " + selected.toString() + " from [" + printHand(players.get(nextPlayer.getPlayerID()).getHand().getCardList()) + "]");
    }

    /**
     * @param nextPlayer The nextPlayer about to preform the follow phase
     * @param trick The current cards played in the trick
     * @param trumps The current trumps suit
     */
    private void follow(Player nextPlayer, Hand trick, Suit trumps)   {
        // Status to be displayed in the status bar
        setStatus(nextPlayer.getStatusText("follow"));
        // Choose a card
        selected = nextPlayer.chooseCard(false, winningCard);
        // Graphically draw a trick card
        drawTrick(trick);

        // Check: Following card must follow suit if possible
        checkSuit(nextPlayer);
        // End Check
        selected.transfer(trick, true); // transfer to trick (includes graphic effect)
        System.out.println("Winning card: " + winningCard.toString());
        System.out.println("Player " + nextPlayer.getPlayerID() + " play: " + selected.toString() + " from [" + printHand(nextPlayer.getHand().getCardList()) + "]");
        // Determine the current winner
        if (currentWinner(winningCard, trumps) ) {
            winner = nextPlayer;
            winningCard = selected;
        }
    }

    /**
     * @param trick Hide the old trick for a new round
     */
    private void removeTrick(Hand trick) {
        delay(600);
        trick.setView(this, new RowLayout(hideLocation, 0));
        trick.draw();
    }

    /**
     * @return A winner of the game if any
     */
    private Optional<Integer> playRound() {
        // Select and display trump suit
        trumps = randomEnum(Suit.class);
        final Actor trumpsActor = new Actor("sprites/" + trumpImage[trumps.ordinal()]);
        addActor(trumpsActor, trumpsActorLocation);

        Hand trick;

        // randomly select player to lead for this round
        Player nextPlayer = players.get(random.nextInt(players.size()));

        // While cards to be played in player hands and no winner declared
        for (int i = 0; i < nbStartCards; i++) {
            trick = new Hand(deck);

            // Player takes the lead
            lead(nextPlayer, trick, trumps);

            // Rest of the players follow
            for (int j = 1; j < nbPlayers; j++) {
                nextPlayer = getNextPlayer(nextPlayer);
                follow(nextPlayer, trick, trumps);
            }
            // Round finished this remove the trick
            removeTrick(trick);
            winningCard = null;

            nextPlayer = winner;
            System.out.println("Winner: " + winner.getPlayerID());
            setStatusText("Player " + winner.getPlayerID() + " wins trick.");
            // Increase the players scores
            winner.increaseScore();
            // Update the graphical images of the score
            updateScore(winner);
            // If there is a winner, return it.
            if (winner.getScore() == winningScore) return Optional.of(winner.getPlayerID());
        }
        removeActor(trumpsActor);
        // If not winner, return empty
        return Optional.empty();
    }

    /**
     * @param filename The file name to access properties from
     */
    private static void setProperties(String filename) {
        Properties config = new Properties();
        // read properties file
        try {
            config.load(new FileInputStream(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // set random seed
        try {
            long seed = Long.parseLong(config.getProperty("seed"));
            random = new Random(seed);
        } catch (NumberFormatException e) {
            random = ThreadLocalRandom.current();
        }
        // set values of other properties
        height = Integer.parseInt(config.getProperty("height", "700"));
        width = Integer.parseInt(config.getProperty("width", "700"));
        fontSize = Integer.parseInt(config.getProperty("fontSize", "36"));
        nbStartCards = Integer.parseInt(config.getProperty("nbStartCards", "13"));
        winningScore = Integer.parseInt(config.getProperty("winningScore", "24"));
        enforceRules = Boolean.parseBoolean(config.getProperty("enforceRules", "false"));
        thinkingTime = Integer.parseInt(config.getProperty("thinkingTime", "2000"));
        nbPlayers = Integer.parseInt(config.getProperty("nbPlayers", "4"));
        // set player properties
        for (int i = 0; i < nbPlayers; i++) {
            if (i == 0) { // player0 is human by default
                playerProperties.add(config.getProperty("player"+i, "human"));
            } else { // all other players are random NPCs by default
                playerProperties.add(config.getProperty("player"+i, "no:random"));
            }
        }
    }

    /**
     * The Whist game
     */
    public Whist() {
        // Derives from jCard frame work
        super(width, height, 30);
        // Set game title bar
        setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        // Status text
        setStatusText("Initializing...");
        // Init players
        initPlayers();
        // Initialise scores to zero
        initScore();
        Optional<Integer> winner;
        // While no winner, keep playing rounds
        do {
            initRound();
            winner = playRound();
        } while (winner.isEmpty());
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText("Game over. Winner is player: " + winner.get());
        refresh();
    }

    // Driver program to set up properties and play the game
    public static void main(String[] args) {
        setProperties("smart.properties");
        new Whist();
    }

}
