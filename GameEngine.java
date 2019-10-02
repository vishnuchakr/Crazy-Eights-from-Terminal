import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Represents the game engine of Crazy 8's.
 */
public class GameEngine {

    /**
     * Constants in the game engine.
     */
    private static final int MINIMUM_PLAYERS = 3;
    private static final int MAXIMUM_PLAYERS = 6;
    private static final int STARTING_HAND_CARDS_NUMBER = 5;

    /**
     * Class variables for the game engine.
     */
    private int numberOfPlayers = 0;
    private Stack<Card> deck = Card.getDeck();
    private boolean isGameOngoing = true;
    private Stack<Card> drawPile = new Stack<>();
    private Stack<Card> discardPile = new Stack<>();
    private Card.Suit currentSuit;
    private List<Player> players = new ArrayList<>();

    /**
     * Getter for the players.
     *
     * @return Players.
     */
    public List<Player> getPlayers() {
        return players;
    }


    /**
     * Method that allows for individual games to be played.
     */
    public void playGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Crazy 8's! How many bots would you like to play against? " + 
                            "You can play against 2 bots, or against up to 5: ");
        numberOfPlayers = scanner.nextInt() + 1;
        while (numberOfPlayers < MINIMUM_PLAYERS || numberOfPlayers > MAXIMUM_PLAYERS) {
            //If the given number of players is invalid, the game cannot be played.
            System.out.println("That is an invalid number of bots. Please enter a number between 1 and 5.");
            Scanner scanner4 = new Scanner(System.in);
            numberOfPlayers = scanner4.nextInt() + 1;
        }

        //Initialize players. The game cannot be played with less than 4 or more than 9 players.
        letPlayersJoinMatch(numberOfPlayers);
        System.out.println("\nA new game has begun.");
        shuffleDeck(deck);
        dealCards();
        drawPile = deck;

        //If the top card of the draw pile is an 8, shuffle it back and draw another card to discard.
        while (drawPile.peek().getRank().equals(Card.Rank.EIGHT)) {
            shuffleDeck(drawPile);
        }

        //Discard the top card of the draw pile and set the current suit.
        currentSuit = drawPile.peek().getSuit();
        discardPile.push(drawPile.pop());
        while (isGameOngoing) {
            playersPlayTurns();
        }

    }

    /**
     * Loops through all of the players and allows them to play their turn.
     */
    private void playersPlayTurns() {

        //When win or tie, change isGameOngoing to false;
        for (Player player : players) {

            System.out.println("\nThe card on top of the discard pile is a " + discardPile.peek().getRank() + 
                                    " of " + discardPile.peek().getSuit() + " and the declared suit to match is " + currentSuit);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.print("");
            }
            if(player.getPlayerId() != numberOfPlayers) {

                botTurns(player);
                if (!isGameOngoing) break;

            } else {
                
                userTurn(player);
                if (!isGameOngoing) break;

            }
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            System.out.print("");
        }
        if (isGameOngoing) System.out.println("\n\n----------NEW TURN----------\n\n");
    }

    /**
     * Allows a bot to play their turn.
     * 
     * @param player The bot playing the turn.
     */
    private void botTurns(Player player) {
        //If bot should draw a card, draw. Else, play a card.
        if (player.shouldDrawCard(discardPile.peek(), currentSuit)) {
            if (drawPile.size() == 0) {
                isGameOngoing = false;
                System.out.println("The draw pile is out of cards. The game ends in a tie.");
                return;
            }
            player.receiveCard(drawPile.pop());
            System.out.println("Player " + player.getPlayerId() + " has drawn a card.");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.print("");
            }

            
        } else {

            //Check if the card played is legal, then play it.
            Card playedCard = player.playCard();
            
            discardPile.push(playedCard);
            System.out.println("Player " + player.getPlayerId() + " has played a " + playedCard.getRank() + " of " + playedCard.getSuit());
            currentSuit = playedCard.getSuit();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.print("");
            }
            //Check if a player has won. If the game is won, end the game.
            checkWonGame(player);
            if (!isGameOngoing) {
                return;
            }

            //If the player just played an 8, bot gets to declare the next suit.
            if (discardPile.peek().getRank().equals(Card.Rank.EIGHT)) {
                currentSuit = player.declareSuit();
                System.out.println("\nBecause this player played an eight, they've declared the new suit to match to be " + currentSuit);
            }
        }
    }

    /**
     * Allows for user input for the turn.
     * 
     * @param player The user to play the turn.
     */
    private void userTurn(Player player) {
        System.out.println("\nThe cards in your hand are: ");
        for (Card card : player.getHand()) {
            System.out.println(card.getRank() + " of " + card.getSuit());
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            System.out.print("");
        }

        List<Card> playableCards = new ArrayList<>();
        System.out.println("\nThe cards that you can play are: ");
        for (Card card : player.getHand()) {
            if (card.getRank() == discardPile.peek().getRank() || card.getSuit() == currentSuit
                || card.getRank() == Card.Rank.EIGHT) {
                playableCards.add(card);
                System.out.println(card.getRank() + " of " + card.getSuit());
            }
        }

        if (playableCards.size() == 0) {
            System.out.println("NOTHING");
            System.out.println("\nBecause you have no legally playable cards, you must draw a card.");
            Card drawnCard = drawPile.peek();
            player.receiveCard(drawPile.pop());
            System.out.println("\nThe card that you've drawn is a " + drawnCard.getRank() + " of " + drawnCard.getSuit());
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.print("");
            }
            return;
        }
        
        System.out.println("\nAs a reminder, the card on top of the discard pile is a " + discardPile.peek().getRank() + 
                            " of " + discardPile.peek().getSuit() + " and the current suit to match is " + currentSuit);

        Scanner scanner2 = new Scanner(System.in);
        System.out.println("\nChoose which card that youd like to play -- do so by entering the card in this format:" +
                            "\"RANK of SUIT\". For example, enter \"ACE of DIAMONDS\" or \"ace of diamonds\" or " +
                            "\"seven of clubs\"");

        String[] cardToPlay = scanner2.nextLine().toUpperCase().split(" ");
        while(invalidCardInput(cardToPlay)) {
            System.out.println("\nThe format which you entered does not match a valid card.");
            System.out.println("\nChoose which card that youd like to play -- do so by entering the card in this format:" +
                "\"RANK of SUIT\". For example, enter \"ACE of DIAMONDS\" or \"ace of diamonds\" or " +
                "\"seven of clubs\"");
            System.out.println("\nThe cards that you can play are: ");
            for (Card card : player.getHand()) {
                if (card.getRank() == discardPile.peek().getRank() || card.getSuit() == discardPile.peek().getSuit()) {
                    playableCards.add(card);
                    System.out.println(card.getRank() + " of " + card.getSuit());
                }
            }
            Scanner scanner3 = new Scanner(System.in);
            cardToPlay = scanner3.nextLine().toUpperCase().split(" ");
        }

        for (Card card : playableCards) {

            if (card.getRank().name().toUpperCase().equals(cardToPlay[0]) 
                && card.getSuit().name().toUpperCase().equals(cardToPlay[2])) {

                player.getHand().remove(card);
                discardPile.push(card);
                System.out.println("\nYou have played a " + card.getRank() + " of " + card.getSuit());
                currentSuit = card.getSuit();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.print("");
                }

                //Check if a player has won. If the game is won, end the game.
                checkWonGame(player);
                if (!isGameOngoing) {
                    return;
                }

                if (card.getRank() == Card.Rank.EIGHT) {
                    System.out.println("\nBecause you've played an eight, you can pick the next suit. " +
                                        "Enter the suit that you'd like (Diamonds, Clubs, Spades, Hearts): ");
                    currentSuit = Card.Suit.valueOf(scanner2.nextLine().toUpperCase()); 
                    System.out.println("The new declared suit to match is " + currentSuit);
                }
                break;
            }
        }
    }

    /**
     * Check if the given player has an empty hand.
     *
     * @param player The player to be checked.
     */
    private void checkWonGame(Player player) {

        //If a player wins, update his/her points accordingly.
        if (player.getHand().size() == 0) {
            isGameOngoing = false;
            if (player.getPlayerId() != numberOfPlayers) {
                System.out.println("Player " + player.getPlayerId() + " has won the game. Better luck next time");
            } else {
                System.out.println("You've succesfully discarded all of your cards and won the game!");
            }
        }
    }

    /**
     * Checks if the user inputted card is valid.
     * 
     * @param input
     * @return Whether or not the given card is valid.
     */
    private boolean invalidCardInput(String[] cardToPlay) {
        if (cardToPlay.length != 3) {
            return true;
        }

        boolean invalidRankOrSuit = false;
        for (Card.Rank rank : Card.Rank.values()) {
            try {
                invalidRankOrSuit = !(Card.Rank.valueOf(cardToPlay[0]) == rank);
            } catch (IllegalArgumentException e) {
                return true;
            }
        }
        for (Card.Suit suit : Card.Suit.values()) {
            try {
                invalidRankOrSuit = !(Card.Suit.valueOf(cardToPlay[2]) == suit);
            } catch (IllegalArgumentException e) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a player given an id.
     *
     * @param id The id of the player to be located.
     * @return The located player.
     */
    public Player getPlayerFromId(int id) {
        for (Player player : players) {
            if (player.getPlayerId() == id) {
                return player;
            }
        }
        return null;
    }

    /**
     * Allows players to join the match at the start.
     *
     * @param numberOfPlayers The number of players to play Crazy 8's.
     * @return The number of players.
     */
    public int letPlayersJoinMatch(int numberOfPlayers) {

        //Initialize the amount of players given to the method and add them to a list of players.
        int playerID;
        List<Integer> opponentIds = new ArrayList<>();
        System.out.println();
        for (playerID = 1; playerID < numberOfPlayers; playerID++) {
            opponentIds = new ArrayList<>();

            for (int opponentID = 1; opponentID <= numberOfPlayers; opponentID++) {
                if (opponentID != playerID) {
                    opponentIds.add(opponentID);
                }
            }

            Player player = new Player();
            player.init(playerID, opponentIds);
            players.add(player);
            System.out.println("Player " + player.getPlayerId() + " has joined the game.");
        }
        Player user = new Player();
        user.init(playerID, opponentIds);
        players.add(user);
        System.out.println("You have joined the game!");

        String lineSeparation = "------------------------------ \n";
        System.out.println(lineSeparation);
        return numberOfPlayers;
    }

    /**
     * Deal each player 5 cards.
     */
    private void dealCards() {
        for (Player player : players) {

            List<Card> hand = new ArrayList<>();
            for (int i = 0; i < STARTING_HAND_CARDS_NUMBER; i++) {
                hand.add(deck.pop());
            }
            player.receiveInitialCards(hand);
        }
    }

    /**
     * Shuffles a given deck
     *
     * @param deck The deck to be shuffled.
     * @return The shuffled deck.
     */
    public Stack<Card> shuffleDeck(Stack<Card> deck) {
        Collections.shuffle(deck);
        return deck;
    }

}
