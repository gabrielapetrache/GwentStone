package components;

import cards.HeroCard;
import cards.MinionCard;
import cardsStorage.Deck;
import cardsStorage.Hand;
import cardsStorage.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Random;

import static java.util.Collections.shuffle;
import static utils.Strings.PLACECARD;
import static utils.Strings.ONE;
import static utils.Strings.TWO;
import static utils.Strings.THREE;
import static utils.Strings.ZERO;


public class Player {
    private int mana;
    private final int nrDecks;
    private final Deck deck;
    private final HeroCard hero;
    private final Hand hand;

    public Player(final int nrDecks, final Deck playingDeck,
                  final HeroCard hero, final Hand hand) {
        mana = 0;
        this.nrDecks = nrDecks;
        this.deck = playingDeck;
        this.hero = hero;
        this.hand = hand;
    }

    /**
     *
     * @param round int
     */
    public void increaseMana(final int round) {
        mana += Math.min(round, 10);
    }

    /**
     *
     * @param num int
     */
    public void decreaseMana(final int num) {
       mana = mana - num;
       if (mana < 0) {
           mana = 0;
       }
    }

    /**
     *
     * @param seed int
     */
    public void shuffleDeck(final int seed) {
        shuffle(deck.getCards(), new Random(seed));
    }

    /**
     * add card in hand
     */
    public void addCardInHand() {
        if (!deck.getCards().isEmpty()) {
            hand.setCardInHand(deck.getCards().get(0));
            deck.getCards().remove(0);
        }
    }

    /**
     *
     * @param table game table
     * @param card card to place
     * @param handIndex int
     * @param playerTurn int
     * @param output out
     * @return error status
     */
    public int placeCardCheck(final Table table, final MinionCard card, final int handIndex,
                         final int playerTurn, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        int rowToAdd = 1;
        node.put("command", PLACECARD);
        node.put("handIdx", handIndex);

        if (card.getMana() > this.mana) {
            node.put("error", "Not enough mana to place card on table.");
            output.add(node);
            return 1;
        }

        if (card.getName().matches("Sentinel|Berserker|The Cursed One|Disciple")) {
            if ((playerTurn == ONE && table.rowIsFull(THREE))
                    || (playerTurn == TWO && table.rowIsFull(ZERO))) {
                node.put("error", "Cannot place card on table since row is full.");
                output.add(node);
                return 1;
            } else if (playerTurn == ONE) {
                rowToAdd = THREE;
            } else if (playerTurn == TWO) {
                rowToAdd = 0;
            }
        }

        if (card.getName().matches("The Ripper|Miraj|Goliath|Warden")) {
            if ((playerTurn == ONE && table.rowIsFull(TWO))
                    || (playerTurn == TWO && table.rowIsFull(ONE))) {
                node.put("error", "Cannot place card on table since row is full.");
                output.add(node);
                return 1;
            } else if (playerTurn == 1) {
                rowToAdd = 2;
            } else if (playerTurn == 2) {
                rowToAdd = 1;
            }
        }
        table.addCard(rowToAdd, card);
        return 0;
    }

    /**
     *
     * @param round int
     */
    public void newRound(final int round) {
        this.addCardInHand();
        this.increaseMana(round);
        this.getHero().setUsed(0);
    }

    /**
     *
     * @param mana int
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     *
     * @return mana
     */
    public int getMana() {
        return mana;
    }

    /**
     *
     * @return nrdecks
     */
    public int getNrDecks() {
        return nrDecks;
    }

    /**
     *
     * @return deck
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     *
     * @return hero
     */
    public HeroCard getHero() {
        return hero;
    }

    /**
     *
     * @return hand
     */
    public Hand getHand() {
        return hand;
    }
}
