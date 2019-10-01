import java.util.*;

/**
 * Class that represents a player in Crazy 8's
 */
public class Player implements PlayerStrategy {

    /**
     * Class variables.
     */
    private int score;
    private boolean isReported = false;
    private int playerId;
    private List<Card> hand;
    private List<Integer> opponentIds;
    private Card.Rank currentRank;
    private Card.Suit currentSuit;

    /**
     * Getters and Setters.
     */
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isReported() {
        return isReported;
    }

    public void setReported(boolean reported) {
        isReported = reported;
    }

    public int getPlayerId() {
        return playerId;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Integer> getOpponentIds() {
        return opponentIds;
    }

    /**
     * Gives the player their assigned id, as well as a list of the opponents' assigned ids.
     *
     * @param playerId The id for this player
     * @param opponentIds A list of ids for this player's opponents
     */
    @Override
    public void init(int playerId, List<Integer> opponentIds) {
        this.playerId = playerId;
        this.opponentIds = opponentIds;
    }

    /**
     * Called at the very beginning of the game to deal the player their initial cards.
     *
     * @param cards The initial list of cards dealt to this player
     */
    @Override
    public void receiveInitialCards(List<Card> cards) {
        this.hand = cards;
    }

    /**
     * Called to check whether the player wants to draw this turn. Gives this player the top card of
     * the discard pile at the beginning of their turn, as well as an optional suit for the pile in
     * case a "8" was played, and the suit was changed.
     *
     * By having this return true, the game engine will then call receiveCard() for this player.
     * Otherwise, playCard() will be called.
     *
     * @param topPileCard The card currently at the top of the pile
     * @param pileSuit The suit that the pile was changed to as the result of an "8" being played.
     * Will be null if no "8" was played.
     * @return whether or not the player wants to draw
     */
    @Override
    public boolean shouldDrawCard(Card topPileCard, Card.Suit pileSuit) {
        currentRank = topPileCard.getRank();
        currentSuit = topPileCard.getSuit();

        if (pileSuit != null) {
            currentSuit = pileSuit;
        }
        for (Card card : this.hand) {
            boolean canDiscardCard = (card.getRank().equals(Card.Rank.EIGHT))
                    || card.getRank().equals(currentRank) || card.getSuit().equals(currentSuit);

            if (canDiscardCard) {
                return false;
            }
        }
        return true;
    }

    /**
     * Called when this player has chosen to draw a card from the deck.
     *
     * @param drawnCard The card that this player has drawn
     */
    @Override
    public void receiveCard(Card drawnCard) {
        this.hand.add(drawnCard);
    }

    /**
     * Called when this player is ready to play a card (will not be called if this player drew on
     * their turn).
     *
     * This will end this player's turn.
     *
     * @return The card this player wishes to put on top of the pile
     */
    @Override
    public Card playCard() {

        //try to play common card, else play an 8.
        //when the player plays a card, remove it from this hand.
        for (Card card : this.hand) {
            boolean isCommonCard = !(card.getRank().equals(Card.Rank.EIGHT))
                    && (card.getRank().equals(currentRank) || card.getSuit().equals(currentSuit));

            if (isCommonCard) {
                this.hand.remove(card);
                return card;
            } else if (card.getRank().equals(Card.Rank.EIGHT)) {
                this.hand.remove(card);
                return card;
            }
        }
        return null;
    }

    /**
     * Called if this player decided to play a "8" card. This player should then return the
     * Card.Suit enum that it wishes to set for the discard pile.
     */
    @Override
    public Card.Suit declareSuit() {

        //Declare the suit that the player has the most of.
        Map<Card.Suit, Integer> mostCommonSuit = new HashMap<>();
        Card.Suit suit;
        mostCommonSuit.put(Card.Suit.DIAMONDS, 0);
        mostCommonSuit.put(Card.Suit.HEARTS, 0);
        mostCommonSuit.put(Card.Suit.SPADES, 0);
        mostCommonSuit.put(Card.Suit.CLUBS, 0);

        for (Card card : this.hand) {

            if (card.getSuit().equals(Card.Suit.DIAMONDS)) {
                suit = Card.Suit.DIAMONDS;
                incrementSuitOccurrences(mostCommonSuit, suit);
            } else if (card.getSuit().equals(Card.Suit.HEARTS)) {
                suit = Card.Suit.HEARTS;
                incrementSuitOccurrences(mostCommonSuit, suit);
            } else if (card.getSuit().equals(Card.Suit.SPADES)) {
                suit = Card.Suit.SPADES;
                incrementSuitOccurrences(mostCommonSuit, suit);
            } else {
                suit = Card.Suit.CLUBS;
                incrementSuitOccurrences(mostCommonSuit, suit);
            }
        }

        //Return the most common suit in the player's hand
        Card.Suit commonSuit = Card.Suit.DIAMONDS;
        int suitOccurrences = 0;

        for (Map.Entry<Card.Suit, Integer> entry : mostCommonSuit.entrySet()) {
            if (entry.getValue() > suitOccurrences) {
                commonSuit = entry.getKey();
                suitOccurrences = entry.getValue();
            }
        }
        return commonSuit;
    }

    /**
     * Called when the game is being reset for the next round.
     */
    @Override
    public void reset() {
        this.hand = new ArrayList<>();
    }

    /**
     * Increments the occurrences of a suit in the given map.
     *
     * @param suitMap Map of suits.
     * @param suit The suit to be counted.
     */
    private void incrementSuitOccurrences(Map<Card.Suit, Integer> suitMap, Card.Suit suit) {
        int increment = suitMap.get(suit);
        increment++;
        suitMap.put(suit, increment);
    }
}
