package cards;

import cardsStorage.Table;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class Card {
    private final int mana = 0;
    private final String description = null;
    private final ArrayList<String> colors = null;
    private final String name = null;
    private int health, attackDamage;
    private int frozen = 0;
    private final int frozenBy = 0;
    private final int roundFrozen = 0;
    private final int used = 0;

    /**
     * Class Card is used for parenting all its inheritors
     */
    public Card() {
    }

    /**
     * decreases mana of card by 1
     */
    public void decreaseMana() {
    }

    /**
     *
     * @return health status
     */
    public int decreaseHealth() {
        return 1;
    }

    /**
     *
     * @param damage for attack
     * @return status after attack
     */
    public int getsAttacked(final int damage) {
        return 0;
    }

    /**
     *
     * @param damage
     * reduces attack damage by damage
     */
    public void attackAttack(final int damage) {
    }

    /**
     *
     * @param attacked card
     * @return error status
     */
    public int useMinionAbility(final Card attacked) {
        return -1;
    }

    /**
     *
     * @param row to use on
     * @param table game table
     * @param turn player turn
     * @param index index
     * @param mana mana player
     * @param round round game
     * @param output out
     * @return error status
     */
    public int useEnvironmentAbility(final int row, final Table table, final int turn,
                                     final int index, final int mana, final int round,
                                     final ArrayNode output) {
        return -1;
    }

    /**
     *
     * @return true if tank
     */
    public boolean isTank() {
        return false;
    }

    /**
     *
     * @param turn player turn
     * @param round round game
     * freezes card
     */
    public void freezeCard(final int turn, final int round) {
    }

    /**
     *
     * @return frozen status
     */
    public int isFrozen() {
        return 0;
    }

    /**
     *
     * @param startingPlayer starting player index
     * @param playerTurn current turn
     * @param round current round
     * unfreezes card if round has passed
     */
    public void unfreezeCardNewRound(final int startingPlayer, final int playerTurn,
                                     final int round) {
    }

    /**
     *
     * @return card placed
     */
    public Card placeCard() {
        return null;
    }

    /**
     *
     * @return output to be printed
     */
    public ObjectNode printOutput() {
        return null;
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
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return color list
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return health
     */
    public int getHealth() {
        return health;
    }

    /**
     *
     * @return damage
     */
    public int getAttackDamage() {
        return 0;
    }

    /**
     *
     * @return status
     */
    public int getUsed() {
        return used;
    }

    /**
     *
     * @param used used
     */
    public void setUsed(final int used) {
    }

    /**
     *
     * @param health set health
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     *
     * @param frozen status
     */
    public void setFrozen(final int frozen) {
        this.frozen = frozen;
    }

    /**
     *
     * @param attackDamage damage
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     *
     * @return status
     */
    public int getFrozen() {
        return frozen;
    }

    /**
     *
     * @return player
     */
    public int getFrozenBy() {
        return frozenBy;
    }

    /**
     *
     * @return round
     */
    public int getRoundFrozen() {
        return roundFrozen;
    }
}
