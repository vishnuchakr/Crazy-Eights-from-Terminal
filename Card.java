import java.util.EnumSet;
import java.util.List;
import java.util.Stack;

/**
 * Represents a standard playing card from a 52 card deck.
 */
public class Card {

    enum Suit {DIAMONDS, HEARTS, SPADES, CLUBS}

    enum Rank {
        ACE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING
    }

    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    /**
     * Returns a list of the standard 52 cards in an unshuffled card deck.
     *
     * @return A list representing a standard deck
     */
    public static Stack<Card> getDeck() {
        Stack<Card> cardDeck = new Stack<>();

        for (Suit suit : EnumSet.allOf(Suit.class)) {
            for (Rank rank : EnumSet.allOf(Rank.class)) {
                cardDeck.push(new Card(suit, rank));
            }
        }
        return cardDeck;
    }
}
