package cardsStorage;

import cards.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class Deck {
    private final ArrayList<Card> cards;

    public Deck(final ArrayList<Card> cardsInDeck) {
        cards = cardsInDeck;
    }

    /**
     *
     * @return cards
     */
    public ArrayList<Card> getCards() {
        return cards;
    }


    /**
     *
     * @return deck
     */
    public ArrayNode printDeck() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode deck = mapper.createArrayNode();
        for (Card card : cards) {
            ObjectNode node = card.printOutput();
            deck.add(node);
        }
        return deck;
    }

}
