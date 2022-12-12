package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static utils.Strings.GETDECK;
import static utils.Strings.GETHERO;
import static utils.Strings.GETTURN;
import static utils.Strings.TABLE;
import static utils.Strings.HANDCARDS;
import static utils.Strings.MANA;
import static utils.Strings.GETENVI;

public class OutputPrinters {

    public OutputPrinters() {
    }

    /**
     *
     * @param idx int
     * @param deck int
     * @return output helper
     */
    public ObjectNode printDeckCommand(final int idx, final ArrayNode deck) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode command = mapper.createObjectNode();
        command.put("command", GETDECK);
        command.put("playerIdx", idx);
        command.set("output", deck);
        return command;
    }

    /**
     *
     * @param idx at idx
     * @param hero to print
     * @return output helper
     */
    public ObjectNode printHeroCommand(final int idx, final ObjectNode hero) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode command = mapper.createObjectNode();
        command.put("command", GETHERO);
        command.put("playerIdx", idx);
        command.set("output", hero);
        return command;
    }

    /**
     *
     * @param turn turn
     * @return output helper
     */
    public ObjectNode printPlayerTurn(final int turn) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode command = mapper.createObjectNode();
        command.put("command", GETTURN);
        command.put("output", turn);
        return command;
    }

    /**
     *
     * @param table game table
     * @return output helper
     */
    public ObjectNode printTable(final ArrayNode table) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode command = mapper.createObjectNode();
        command.put("command", TABLE);
        command.set("output", table);
        return command;
    }

    /**
     *
     * @param idx int
     * @param hand to print
     * @return output helper
     */
    public ObjectNode printHandCommand(final int idx, final ArrayNode hand) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode command = mapper.createObjectNode();
        command.put("command", HANDCARDS);
        command.put("playerIdx", idx);
        command.set("output", hand);
        return command;
    }

    /**
     *
     * @param mana int
     * @param index int
     * @return output helper
     */
    public ObjectNode printPlayerMana(final int mana, final int index) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode command = mapper.createObjectNode();
        command.put("command", MANA);
        command.put("playerIdx", index);
        command.put("output", mana);
        return command;
    }

    /**
     *
     * @param idx int
     * @param hand int
     * @return output helper
     */
    public ObjectNode printEnviCommand(final int idx, final ArrayNode hand) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode command = mapper.createObjectNode();
        command.put("command", GETENVI);
        command.put("playerIdx", idx);
        command.set("output", hand);
        return command;
    }
}
