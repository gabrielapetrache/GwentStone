package cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class MinionCard extends Card {
    private int mana;
    private int attackDamage;
    private int health;
    private final String description;
    private final ArrayList<String> colors;
    private final String name;
    private int frozen = 0;
    private int frozenBy = 0;
    private int roundFrozen = 0;
    private int used = 0;

    public MinionCard(final int mana, final int attackDamage, final int health,
                      final String description, final ArrayList<String> color, final String name) {
        this.attackDamage = attackDamage;
        this.mana = mana;
        this.health = health;
        this.description = description;
        this.colors = color;
        this.name = name;
    }

    /**
     *
     * @param attacked card attacked
     * @return error status
     */
    public int useMinionAbility(final Card attacked) {
        if (attacked != null) {
            if (this.getName().equals("The Ripper")) {
                attacked.attackAttack(2);
            }
            if (this.getName().equals("Miraj")) {
                int lifeAttacker = this.getHealth();
                int lifeAttacked = attacked.getHealth();
                this.setHealth(lifeAttacked);
                attacked.setHealth(lifeAttacker);
            }
            if (this.getName().equals("The Cursed One")) {
                int lifeAttacked = attacked.getHealth();
                int damageAttacked = attacked.getAttackDamage();
                attacked.setHealth(damageAttacked);
                attacked.setAttackDamage(lifeAttacked);
            }
            if (this.getName().equals("Disciple")) {
                attacked.setHealth(attacked.getHealth() + 2);
            }
            if (attacked.getHealth() <= 0) {
                return 1;
            }
        }
        return 0;
    }

    /**
     *
     * @return true if tank
     */
    public boolean isTank() {
        return this.name.equals("Goliath") || this.name.equals("Warden");
    }

    /**
     *
     * @return card
     */
    public Card placeCard() {
        return this;
    }

    /**
     *
     * @param turn player turn
     * @param round current round
     * freezes card
     */
    public void freezeCard(final int turn, final int round) {
        frozen = 1;
        frozenBy = turn;
        roundFrozen = round;
    }

    /**
     *
     * @param startingPlayer starting player
     * @param playerTurn curr turn
     * @param round int
     * unfreezes card if round has passed
     */
    public void unfreezeCardNewRound(final int startingPlayer, final int playerTurn,
                                     final int round) {
        if ((round - roundFrozen >= 1) && (startingPlayer == frozenBy)) {
            setFrozen(0);
        } else if ((startingPlayer != frozenBy) && (playerTurn == frozenBy)) {
            setFrozen(0);
        }
    }

    /**
     *
     * @param damage
     * reduces attack damage by damage
     */
    public void attackAttack(final int damage) {
        attackDamage = attackDamage - damage;
        if (attackDamage <= 0) {
            attackDamage = 0;
        }
    }

    /**
     *
     * @return true if frozen
     */
    public int isFrozen() {
        if (frozen == 1) {
            return 1;
        }
        return 0;
    }

    /**
     *
     * @return 0 if dead
     */
    public int decreaseHealth() {
        this.health = this.health - 1;
        if (this.health <= 0) {
            return 0;
        }
        return 1;
    }

    /**
     *
     * @param damage attack
     * @return 1 if dead
     */
    public int getsAttacked(final int damage) {
        health = health - damage;
        if (health <= 0) {
            return 1;
        }
        return 0;
    }

    /**
     * decreases mana
     */
    public void decreaseMana() {
        this.mana = this.mana - 1;
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
        return attackDamage;
    }

    /**
     *
     * @return colors
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
     * @return if frozen
     */
    public int getFrozen() {
        return frozen;
    }

    /**
     *
     * @return use status
     */
    public int getUsed() {
        return used;
    }

    /**
     *
     * @param used
     * set use status
     */
    public void setUsed(final int used) {
        this.used = used;
    }

    /**
     *
     * @param health status
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     *
     * @param attackDamage status
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     *
     * @return output
     */
    public ObjectNode printOutput() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode card1 = mapper.createObjectNode();
        card1.put("mana", mana);
        card1.put("attackDamage", attackDamage);
        card1.put("health", health);
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
