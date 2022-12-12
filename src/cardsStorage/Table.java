package cardsStorage;

import cards.Card;
import cards.MinionCard;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import components.Player;

import static utils.Strings.*;

public class Table {
    private final Card[][] gameTable = new Card[FOUR][FIVE];

    public Table() {
    }

    /**
     * @param row  to add
     * @param card to add
     */
    public void addCard(final int row, final Card card) {
        int column;
        int i = 0;

        if (!rowIsFull(row)) {
            do {
                column = i;
                i++;
            } while (gameTable[row][column] != null && column < FIVE);
            gameTable[row][column] = card;
        }
    }

    /**
     * @param row  to remove from
     * @param card to remove
     */
    public void removeCard(final int row, final Card card) {
        int removeIndex = 0;
        int actualLength = 0;
        for (int i = 0; i < gameTable[row].length; i++) {
            if (gameTable[row][i] != null) {
                actualLength++;
                if (gameTable[row][i].equals(card)) {
                    removeIndex = i;
                }
            }
        }
        Card[] aux = new Card[FIVE];
        for (int i = 0, j = 0; i < actualLength; i++) {
            if (i != removeIndex) {
                aux[j++] = gameTable[row][i];
            }
        }
        gameTable[row] = aux;
    }

    /**
     * @param row to check
     * @return statement
     */
    public boolean rowIsFull(final int row) {
        for (int i = 0; i < FIVE; i++) {
            if (gameTable[row][i] == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param player     current
     * @param indexHand  int
     * @param playerTurn int
     * @param output     output
     */
    public void placeCard(final Player player, final int indexHand, final int playerTurn,
                          final ArrayNode output) {
        if (player.getHand().getCardsInHand().get(indexHand).getHealth() == 0) {
            final ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("command", PLACECARD);
            node.put("handIdx", indexHand);
            node.put("error", "Cannot place environment card on table.");
            output.add(node);
        } else {
            // if the card is valid, it is minion type
            Card cardPlaced = player.getHand().getCardsInHand()
                    .get(indexHand);
            // make a copy of the card, but save it as a minion
            MinionCard copy = new MinionCard(cardPlaced.getMana(),
                    cardPlaced.getAttackDamage(), cardPlaced.getHealth(),
                    cardPlaced.getDescription(), cardPlaced.getColors(),
                    cardPlaced.getName());
            // the placeCard function returns 1 in case of error
            int error = player.placeCardCheck(this, copy, indexHand,
                    playerTurn, output);
            if (error == 0) {
                player.decreaseMana(cardPlaced.getMana());
                player.getHand().getCardsInHand().remove(cardPlaced);
            }
        }
    }

    public void useEnvironment(final Player player, final int indexHand, final int playerTurn,
                               final int indexRow, final int round, final ArrayNode output) {
        final ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", USEENVI);
        node.put("handIdx", indexHand);
        node.put("affectedRow", indexRow);
        node.put("error", "Chosen card is not of type environment.");

        Card usedCard = player.getHand().getCardsInHand().get(indexHand);
        if (usedCard.getHealth() != 0) {
            output.add(node);
            return;
        }
        // use ability and check for errors
        int error = usedCard.useEnvironmentAbility(indexRow, this, playerTurn,
                indexHand, player.getMana(), round, output);
        if (error == 0) {
            // if card is used, it gets removed from hand
            player.decreaseMana(usedCard.getMana());
            player.getHand().getCardsInHand().remove(usedCard);
        }
    }

    /**
     * @param xAttacker x
     * @param yAttacker y
     * @param xAttacked x
     * @param yAttacked y
     * @param output    output
     * @return error
     */
    public int cardAttack(final int xAttacker, final int yAttacker, final int xAttacked,
                          final int yAttacked, final ArrayNode output) {
        Card attacker = gameTable[xAttacker][yAttacker];
        Card attacked = gameTable[xAttacked][yAttacked];

        // if statement used to compile tests 16, 17
        if (attacker != null) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("command", USEATTACK);
            ObjectNode node1 = mapper.createObjectNode();
            node1.put("x", xAttacker);
            node1.put("y", yAttacker);
            node.set("cardAttacker", node1);
            ObjectNode node2 = mapper.createObjectNode();
            node2.put("x", xAttacked);
            node2.put("y", yAttacked);
            node.set("cardAttacked", node2);

            if (((xAttacker == ONE || xAttacker == ZERO)
                    && (xAttacked == ONE || xAttacked == ZERO))
                    || ((xAttacker == TWO || xAttacker == THREE)
                    && (xAttacked == TWO || xAttacked == THREE))) {
                node.put("error", "Attacked card does not belong to the enemy.");
                output.add(node);
                return 1;
            }
            if (attacker.getUsed() == 1) {
                node.put("error", "Attacker card has already attacked this turn.");
                output.add(node);
                return 1;
            }
            if (attacker.isFrozen() == 1) {
                node.put("error", "Attacker card is frozen.");
                output.add(node);
                return 1;
            }
            if (isTankOnRow(xAttacked) && !attacked.isTank()) {
                node.put("error", "Attacked card is not of type 'Tank'.");
                output.add(node);
                return 1;
            }

            int killed = attacked.getsAttacked(attacker.getAttackDamage());
            if (killed == 1) {
                removeCard(xAttacked, attacked);
            }
        }
        return 0;
    }

    /**
     * @param xAttacker x
     * @param yAttacker y
     * @param xAttacked x
     * @param yAttacked y
     * @param output    output
     * @return error
     */
    public int cardAbility(final int xAttacker, final int yAttacker, final int xAttacked,
                           final int yAttacked, final ArrayNode output) {
        Card attacker = gameTable[xAttacker][yAttacker];
        Card attacked = gameTable[xAttacked][yAttacked];

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", USEABILITY);
        ObjectNode node1 = mapper.createObjectNode();
        node1.put("x", xAttacker);
        node1.put("y", yAttacker);
        node.set("cardAttacker", node1);
        ObjectNode node2 = mapper.createObjectNode();
        node2.put("x", xAttacked);
        node2.put("y", yAttacked);
        node.set("cardAttacked", node2);

        if (attacker != null) {
            if (attacker.isFrozen() == 1) {
                node.put("error", "Attacker card is frozen.");
                output.add(node);
                return 1;
            }
            if (attacker.getUsed() == 1) {
                node.put("error", "Attacker card has already attacked this turn.");
                output.add(node);
                return 1;
            }
            if (attacker.getName().equals("Disciple")) {
                if (((xAttacker == ONE || xAttacker == ZERO)
                        && (xAttacked == TWO || xAttacked == THREE))
                        || ((xAttacker == TWO || xAttacker == THREE)
                        && (xAttacked == ZERO || xAttacked == ONE))) {
                    node.put("error", "Attacked card does not belong to the current player.");
                    output.add(node);
                    return 1;
                }
            }
            if (attacker.getName().matches("The Ripper|Miraj|The Cursed One")) {
                if (((xAttacker == ONE || xAttacker == ZERO)
                        && (xAttacked == ONE || xAttacked == ZERO))
                        || ((xAttacker == TWO || xAttacker == THREE)
                        && (xAttacked == TWO || xAttacked == THREE))) {
                    node.put("error", "Attacked card does not belong to the enemy.");
                    output.add(node);
                    return 1;
                }
                if (isTankOnRow(xAttacked) && !attacked.isTank()) {
                    node.put("error", "Attacked card is not of type 'Tank'.");
                    output.add(node);
                    return 1;
                }
            }
            // minion ability may kill another card
            int killed = attacker.useMinionAbility(attacked);
            if (killed != 0) {
                removeCard(xAttacked, attacked);
            }
        }
        return 0;
    }

    /**
     * @param xAttacker x
     * @param yAttacker y
     * @param turn      current
     * @param enemyHero attacked
     * @param output    out
     * @return error
     */
    public int attackHero(final int xAttacker, final int yAttacker, final int turn,
                          final Card enemyHero, final ArrayNode output) {
        Card attacker = gameTable[xAttacker][yAttacker];
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", HEROATTACK);
        ObjectNode node1 = mapper.createObjectNode();
        node1.put("x", xAttacker);
        node1.put("y", yAttacker);
        node.set("cardAttacker", node1);

        if (attacker != null) {

            if (attacker.isFrozen() == 1) {
                node.put("error", "Attacker card is frozen.");
                output.add(node);
                return 1;
            }
            if (attacker.getUsed() == 1) {
                node.put("error", "Attacker card has already attacked this turn.");
                output.add(node);
                return 1;
            }
            if (turn == 1) {
                if (isTankOnRow(ZERO) || isTankOnRow(ONE)) {
                    node.put("error", "Attacked card is not of type 'Tank'.");
                    output.add(node);
                    return 1;
                }
            } else {
                if (isTankOnRow(TWO) || isTankOnRow(THREE)) {
                    node.put("error", "Attacked card is not of type 'Tank'.");
                    output.add(node);
                    return 1;
                }
            }
            int killed = enemyHero.getsAttacked(attacker.getAttackDamage());
            if (killed == 1) {
                if (turn == 1) {
                    ObjectNode node2 = mapper.createObjectNode();
                    node2.put("gameEnded", "Player one killed the enemy hero.");
                    output.add(node2);
                    return 2;
                } else {
                    ObjectNode node2 = mapper.createObjectNode();
                    node2.put("gameEnded", "Player two killed the enemy hero.");
                    output.add(node2);
                    return 2;
                }
            }
        }

        return 0;
    }

    /**
     * @param hero        attacker
     * @param rowAttacked row
     * @param playerMana  mana of attacker
     * @param turn        player turn
     * @param round       game round
     * @param output      output
     * @return error
     */
    public int heroAbility(final Card hero, final int rowAttacked,
                           final int playerMana, final int turn, final int round,
                           final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", HEROABILITY);
        node.put("affectedRow", rowAttacked);

        if (hero != null) {

            if (playerMana < hero.getMana()) {
                node.put("error", "Not enough mana to use hero's ability.");
                output.add(node);
                return 1;
            }
            if (hero.getUsed() == 1) {
                node.put("error", "Hero has already attacked this turn.");
                output.add(node);
                return 1;
            }
            if (hero.getName().matches("Lord Royce|Empress Thorina")) {
                if ((turn == ONE && rowAttacked == TWO)
                        || (turn == ONE && rowAttacked == THREE)
                        || (turn == TWO && rowAttacked == ZERO)
                        || (turn == TWO && rowAttacked == ONE)) {
                    node.put("error", "Selected row does not belong to the enemy.");
                    output.add(node);
                    return 1;
                }
            }
            if (hero.getName().matches("General Kocioraw|King Mudface")) {
                if ((turn == ONE && rowAttacked == ZERO)
                        || (turn == ONE && rowAttacked == ONE)
                        || (turn == TWO && rowAttacked == THREE)
                        || (turn == TWO && rowAttacked == TWO)) {
                    node.put("error", "Selected row does not belong to the current player.");
                    output.add(node);
                    return 1;
                }
            }

            if (hero.getName().equals("Lord Royce")) {
                int indexMaxAttack = 0;
                for (int i = 0; i < getNrCardsOnRow(rowAttacked); i++) {
                    if (getGameTable()[rowAttacked][indexMaxAttack].getAttackDamage()
                            < getGameTable()[rowAttacked][i].getAttackDamage()) {
                        indexMaxAttack = i;
                    }
                }
                getGameTable()[rowAttacked][indexMaxAttack].freezeCard(turn, round);
            }

            if (hero.getName().equals("Empress Thorina")) {
                int indexMaxHealth = 0;
                for (int i = 0; i < getNrCardsOnRow(rowAttacked); i++) {
                    if (getGameTable()[rowAttacked][indexMaxHealth].getHealth()
                            < getGameTable()[rowAttacked][i].getHealth()) {
                        indexMaxHealth = i;
                    }
                }
                removeCard(rowAttacked, getGameTable()[rowAttacked][indexMaxHealth]);
            }

            if (hero.getName().equals("King Mudface")) {
                for (int i = 0; i < getNrCardsOnRow(rowAttacked); i++) {
                    getGameTable()[rowAttacked][i].setHealth(
                            getGameTable()[rowAttacked][i].getHealth() + 1);
                }
            }

            if (hero.getName().equals("General Kocioraw")) {
                for (int i = 0; i < getNrCardsOnRow(rowAttacked); i++) {
                    getGameTable()[rowAttacked][i].setAttackDamage(
                            getGameTable()[rowAttacked][i].getAttackDamage() + 1);
                }
            }
        }
        return 0;
    }

    /**
     * when round ends
     */
    public void resetUse() {
        for (int i = 0; i < FOUR; i++) {
            for (int j = 0; j < getNrCardsOnRow(i); j++) {
                gameTable[i][j].setUsed(0);
            }
        }
    }

    /**
     * @param start starting player
     * @param turn  current player turn
     * @param round round of game
     *              when card gets unfrozen
     */
    public void unfreezeCardsNewRound(final int start, final int turn, final int round) {
        for (int i = 0; i < FOUR; i++) {
            for (int j = 0; j < getNrCardsOnRow(i); j++) {
                if (gameTable[i][j].isFrozen() == 1) {
                    gameTable[i][j].unfreezeCardNewRound(start, turn, round);
                }
            }
        }
    }

    /**
     * @param x x
     * @param y y
     * @return card at position
     */
    public ObjectNode cardAtPos(final int x, final int y) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", CARDPOS);
        node.put("x", x);
        node.put("y", y);
        if (getGameTable()[x][y] == null) {
            node.put("output", "No card available at that position.");
        } else {
            ObjectNode cardAtPos = getGameTable()[x][y].printOutput();
            node.set("output", cardAtPos);
        }
        return node;
    }

    /**
     * @return frozen cards output
     */
    public ObjectNode frozenCards() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", FROZEN);
        ArrayNode cards = mapper.createArrayNode();
        for (int i = 0; i < FOUR; i++) {
            for (int j = 0; j < FIVE; j++) {
                if (getGameTable()[i][j] != null) {
                    if (getGameTable()[i][j].isFrozen() == 1) {
                        cards.add(getGameTable()[i][j].printOutput());
                    }
                }
            }
        }
        node.set("output", cards);
        return node;
    }

    /**
     * @param row to check
     * @return statement
     */
    public boolean isTankOnRow(final int row) {
        if (row == THREE) {
            for (int i = 0; i < getNrCardsOnRow(row - 1); i++) {
                if (getGameTable()[row - 1][i].isTank()) {
                    return true;
                }
            }
        }
        if (row == 0) {
            for (int i = 0; i < getNrCardsOnRow(row + 1); i++) {
                if (getGameTable()[row + 1][i].isTank()) {
                    return true;
                }
            }
        }
        for (int i = 0; i < getNrCardsOnRow(row); i++) {
            if (getGameTable()[row][i].isTank()) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return table
     */
    public ArrayNode printTable() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode table = mapper.createArrayNode();
        for (int i = 0; i < FOUR; i++) {
            ArrayNode row = mapper.createArrayNode();
            for (Card card : gameTable[i]) {
                if (card != null) {
                    ObjectNode node = card.printOutput();
                    row.add(node);
                }
            }
            table.add(row);
        }
        return table;
    }


    /**
     * @param row to get from
     * @return number of cards
     */
    public int getNrCardsOnRow(final int row) {
        int nr = 0;
        for (int i = 0; i < FIVE; i++) {
            if (gameTable[row][i] != null) {
                nr++;
            }
        }
        return nr;
    }

    /**
     * @return game table
     */
    public Card[][] getGameTable() {
        return gameTable;
    }
}


