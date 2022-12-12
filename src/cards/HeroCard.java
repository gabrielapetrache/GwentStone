package cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

import static utils.Strings.HEROHEALTH;

public class HeroCard extends Card {
    private final int mana;
    private int health;
    private final String description;
    private final ArrayList<String> colors;
    private final String name;
    private int used = 0;

    public HeroCard(final int mana, final String description, final ArrayList<String> colors,
                    final String name) {
        this.mana = mana;
        this.health = HEROHEALTH;
        this.description = description;
        this.colors = colors;
        this.name = name;
    }

    /**
     *
     * @param damage attack damage
     * @return life status
     */
    public int getsAttacked(final int damage) {
        health = health - damage;
        if (health <= 0) {
            return 1;
        }
        return 0;
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
     * @return status of card
     */
    public int getUsed() {
        return used;
    }


    /**
     *
     * @param used
     * sets status of card
     */
    public void setUsed(final int used) {
        this.used = used;
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
     * @return card to be printed
     */
    public ObjectNode printOutput() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode card1 = mapper.createObjectNode();
        card1.put("mana", mana);
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

