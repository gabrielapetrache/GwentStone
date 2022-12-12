package cards;

import cardsStorage.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

import static utils.Strings.USEENVI;
import static utils.Strings.ZERO;
import static utils.Strings.ONE;
import static utils.Strings.TWO;
import static utils.Strings.THREE;
import static utils.Strings.FIVE;


public class EnvironmentCard extends Card {
    private final String description;
    private final ArrayList<String> colors;
    private final String name;
    private int mana;

    public EnvironmentCard(final int mana, final String description,
                           final ArrayList<String> colors, final String name) {
        this.mana = mana;
        this.description = description;
        this.colors = colors;
        this.name = name;
    }


    /**
     * applies the ability of the card
     */
    public int useEnvironmentAbility(final int row, final Table table, final int turn,
                                     final int indexHand, final int playerMana,
                                     final int round, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("command", USEENVI);
        node.put("handIdx", indexHand);
        node.put("affectedRow", row);

        if (playerMana < mana) {
            node.put("error", "Not enough mana to use environment card.");
            output.add(node);
            return 1;
        } else if ((turn == ONE && row == TWO) || (turn == ONE && row == THREE)
                || (turn == TWO && row == ZERO) || (turn == TWO && row == ONE)) {
            node.put("error", "Chosen row does not belong to the enemy.");
            output.add(node);
            return 1;
        }
        switch (this.name) {
            case "Firestorm":
                int size = table.getNrCardsOnRow(row);
                int j = 0;
                ArrayList<Card> cardsToRemove = new ArrayList<>();
                while (j < size) {
                    if (table.getGameTable()[row][j] != null) {
                        int res = table.getGameTable()[row][j].decreaseHealth();
                        if (res == 0) {
                            cardsToRemove.add(table.getGameTable()[row][j]);
                        }
                        j++;
                    }
                }
                for (Card remove : cardsToRemove) {
                    table.removeCard(row, remove);
                }
                cardsToRemove.clear();

                break;
            case "Winterfell":

                for (int k = 0; k < FIVE; k++) {
                    if (table.getGameTable()[row][k] != null) {
                        table.getGameTable()[row][k].freezeCard(turn, round);
                    }
                }
                break;
            case "Heart Hound":
                if ((turn == ONE && row == ZERO && table.rowIsFull(THREE))
                        || (turn == ONE && row == ONE && table.rowIsFull(TWO))
                        || (turn == TWO && row == TWO && table.rowIsFull(ONE))
                        || (turn == TWO && row == THREE && table.rowIsFull(ZERO))) {
                    node.put("error", "Cannot steal enemy card since " +
                            "the player's row is full.");
                    output.add(node);
                    return 1;
                }
                Card maxCard = new Card();
                int index = 0;
                for (int k = 0; k < FIVE; k++) {
                    if (table.getGameTable()[row][k] != null) {
                        if (table.getGameTable()[row][k].getHealth() > maxCard.getHealth()) {
                            maxCard = table.getGameTable()[row][k];
                            index = k;
                        }
                    }
                }
                if (row == 0) {
                    table.addCard(THREE, table.getGameTable()[row][index]);
                    table.removeCard(row, table.getGameTable()[row][index]);
                } else if (row == ONE) {
                    table.addCard(TWO, table.getGameTable()[row][index]);
                    table.removeCard(row, table.getGameTable()[row][index]);
                } else if (row == TWO) {
                    table.addCard(ONE, table.getGameTable()[row][index]);
                    table.removeCard(row, table.getGameTable()[row][index]);
                } else if (row == THREE) {
                    table.addCard(ZERO, table.getGameTable()[row][index]);
                    table.removeCard(row, table.getGameTable()[row][index]);
                }
                break;
            default:
        }
        return 0;
    }

    /**
     * decreases card mana by 1
     */
    public void decreaseMana() {
        this.mana = this.mana - 1;
    }


    /**
     * @return mana
     */
    public int getMana() {
        return mana;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return colors
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return output to be printed
     */
    public ObjectNode printOutput() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode card1 = mapper.createObjectNode();
        card1.put("mana", mana);
        card1.put("description", description);
        ArrayNode colors = mapper.createArrayNode();
        for (String color : this.colors) {
            colors.add(color);
        }
        card1.set("colors", colors);
        card1.put("name", name);

        return card1;
    }
}
