package cardsStorage;

import cards.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> cardsInHand = new ArrayList<>();

    public Hand() {
    }

    public Hand(final ArrayList<Card> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    /**
     *
     * @return cards
     */
    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    /**
     *
     * @param cardInHand int
     */
    public void setCardInHand(final Card cardInHand) {
        cardsInHand.add(cardInHand);
    }

    /**
     *
     * @return hand
     */
    public ArrayNode printHand() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode hand = mapper.createArrayNode();
        for (Card card : cardsInHand) {
            ObjectNode node = card.printOutput();
            hand.add(node);
        }
        return hand;
    }

    /**
     *
     * @return envi cards
     */
    public ArrayNode printEnvironment() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode envi = mapper.createArrayNode();
        for (Card card : cardsInHand) {
            if (card.getHealth() == 0) {
                ObjectNode node = card.printOutput();
                envi.add(node);
            }
        }
        return envi;
    }

}
