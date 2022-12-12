package components;

import cards.Card;
import cards.EnvironmentCard;
import cards.HeroCard;
import cards.MinionCard;
import cardsStorage.Deck;
import cardsStorage.Hand;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fileio.CardInput;
import fileio.Input;
import fileio.GameInput;
import fileio.StartGameInput;
import fileio.ActionsInput;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.node.ArrayNode;
import cardsStorage.Table;
import utils.OutputPrinters;

import static utils.Strings.*;

public final class Game {
    private final ArrayList<Deck> firstPlayerDecks = new ArrayList<>();
    private final ArrayList<Deck> secondPlayerDecks = new ArrayList<>();
    private final ArrayList<GameInput> games = new ArrayList<>();
    private final ArrayNode output;
    private int playerOneGamesWon = 0;
    private int playerTwoGamesWon = 0;
    private int gamesPlayed = 0;
    private final ObjectMapper mapper = new ObjectMapper();

    public Game(final Input input, final ArrayNode output) {
        this.output = output;

        for (int i = 0; i < input.getPlayerOneDecks().getNrDecks(); i++) {
            ArrayList<Card> cardsFromCurrDeck = new ArrayList<>();

            for (int j = 0; j < input.getPlayerOneDecks().getNrCardsInDeck(); j++) {
                CardInput currentCard = input.getPlayerOneDecks().getDecks().get(i).get(j);
                // if health is null, then current card is environment type
                if (currentCard.getHealth() == 0) {
                    cardsFromCurrDeck.add(new EnvironmentCard(currentCard.getMana(),
                            currentCard.getDescription(), currentCard.getColors(),
                            currentCard.getName()));
                } else {
                    MinionCard minionCard = new MinionCard(currentCard.getMana(),
                            currentCard.getAttackDamage(), currentCard.getHealth(),
                            currentCard.getDescription(), currentCard.getColors(),
                            currentCard.getName());
                    cardsFromCurrDeck.add(minionCard);
                }
            }
            firstPlayerDecks.add(i, new Deck(cardsFromCurrDeck));
        }

        for (int i = 0; i < input.getPlayerTwoDecks().getNrDecks(); i++) {
            ArrayList<Card> cardsFromCurrDeck = new ArrayList<>();

            for (int j = 0; j < input.getPlayerTwoDecks().getNrCardsInDeck(); j++) {
                CardInput currentCard = input.getPlayerTwoDecks().getDecks().get(i).get(j);

                // if health is null, then current card is environment type
                if (currentCard.getHealth() == 0) {
                    cardsFromCurrDeck.add(new EnvironmentCard(currentCard.getMana(),
                            currentCard.getDescription(), currentCard.getColors(),
                            currentCard.getName()));
                } else {
                    MinionCard minionCard = new MinionCard(currentCard.getMana(),
                            currentCard.getAttackDamage(), currentCard.getHealth(),
                            currentCard.getDescription(), currentCard.getColors(),
                            currentCard.getName());
                    cardsFromCurrDeck.add(minionCard);
                }
            }
            secondPlayerDecks.add(i, new Deck(cardsFromCurrDeck));
        }

        games.addAll(input.getGames());
    }

    /**
     *
     * solves given input
     */
    public void solve() {
        for (GameInput game : games) {
            StartGameInput start = game.getStartGame();
            Table table = new Table();
            ArrayList<ActionsInput> commands = game.getActions();
            OutputPrinters printer = new OutputPrinters();

            int seed = start.getShuffleSeed();

            CardInput heroOneInput = start.getPlayerOneHero();
            HeroCard firstPlayerHero = new HeroCard(heroOneInput.getMana(),
                    heroOneInput.getDescription(), heroOneInput.getColors(),
                    heroOneInput.getName());

            CardInput heroTwoInput = start.getPlayerTwoHero();
            HeroCard secondPlayerHero = new HeroCard(heroTwoInput.getMana(),
                    heroTwoInput.getDescription(), heroTwoInput.getColors(),
                    heroTwoInput.getName());

            Player playerOne = new Player(firstPlayerDecks.size(),
                    firstPlayerDecks.get(start.getPlayerOneDeckIdx()), firstPlayerHero,
                    new Hand());
            Player playerTwo = new Player(secondPlayerDecks.size(),
                    secondPlayerDecks.get(start.getPlayerTwoDeckIdx()), secondPlayerHero,
                    new Hand());

            playerOne.shuffleDeck(seed);
            playerTwo.shuffleDeck(seed);

            int startingPlayer = start.getStartingPlayer();
            int playerTurn = startingPlayer;
            int round = 1;
            playerTwo.increaseMana(round);
            playerOne.increaseMana(round);

            playerOne.addCardInHand();
            playerTwo.addCardInHand();

            for (ActionsInput curr : commands) {
                ObjectNode objectNode = mapper.createObjectNode();

                switch (curr.getCommand()) {
                    case GETDECK:
                        int idx = curr.getPlayerIdx();
                        ArrayNode deck;
                        if (idx == 1) {
                            deck = playerOne.getDeck().printDeck();
                        } else {
                            deck = playerTwo.getDeck().printDeck();
                        }
                        output.add(printer.printDeckCommand(idx, deck));
                        break;

                    case GETHERO:
                        int indx = curr.getPlayerIdx();
                        ObjectNode hero;
                        if (indx == 1) {
                            hero = playerOne.getHero().printOutput();
                        } else {
                            hero = playerTwo.getHero().printOutput();
                        }
                        output.add(printer.printHeroCommand(indx, hero));
                        break;

                    case GETTURN:
                        output.add(printer.printPlayerTurn(playerTurn));
                        break;

                    case ENDTURN:
                        if (playerTurn == 1) {
                            playerTurn = 2;
                            table.unfreezeCardsNewRound(startingPlayer, playerTurn, round);
                            // if starting player equals player turn, a new round has begun
                            if (startingPlayer == 2) {
                                round++;
                                table.unfreezeCardsNewRound(startingPlayer, playerTurn, round);
                                playerOne.newRound(round);
                                playerTwo.newRound(round);
                                table.resetUse();
                            }
                        } else {
                            playerTurn = 1;
                            table.unfreezeCardsNewRound(startingPlayer, playerTurn, round);
                            if (startingPlayer == 1) {
                                round++;
                                table.unfreezeCardsNewRound(startingPlayer, playerTurn, round);
                                playerOne.newRound(round);
                                playerTwo.newRound(round);
                                table.resetUse();
                            }
                        }
                        break;
                    case PLACECARD:
                        int handIdx = curr.getHandIdx();
                        if (playerTurn == 1) {
                            table.placeCard(playerOne, handIdx, playerTurn, output);
                        } else {
                            table.placeCard(playerTwo, handIdx, playerTurn, output);
                        }
                        break;
                    case TABLE:
                        ArrayNode printTable = table.printTable();
                        output.add(printer.printTable(printTable));
                        break;
                    case HANDCARDS:
                        int handindex = curr.getPlayerIdx();
                        ArrayNode hand;
                        if (handindex == 1) {
                            hand = playerOne.getHand().printHand();
                        } else {
                            hand = playerTwo.getHand().printHand();
                        }
                        output.add(printer.printHandCommand(handindex, hand));
                        break;
                    case MANA:
                        int manaIndex = curr.getPlayerIdx();
                        if (manaIndex == 1) {
                            output.add(printer.printPlayerMana(playerOne.getMana(), manaIndex));
                        } else {
                            output.add(printer.printPlayerMana(playerTwo.getMana(), manaIndex));
                        }
                        break;
                    case GETENVI:
                        int enviIndex = curr.getPlayerIdx();
                        ArrayNode envi;
                        if (enviIndex == 1) {
                            envi = playerOne.getHand().printEnvironment();
                        } else {
                            envi = playerTwo.getHand().printEnvironment();
                        }
                        output.add(printer.printEnviCommand(enviIndex, envi));
                        break;
                    case USEENVI:
                        int indexRow = curr.getAffectedRow();
                        int indexHand = curr.getHandIdx();

                        if (playerTurn == 1) {
                            table.useEnvironment(playerOne, indexHand, playerTurn, indexRow, round, output);
                        } else {
                            table.useEnvironment(playerTwo, indexHand, playerTurn, indexRow, round, output);
                        }
                        break;
                    case CARDPOS:
                        int indexX = curr.getX();
                        int indexY = curr.getY();
                        output.add(table.cardAtPos(indexX, indexY));
                        break;
                    case FROZEN:
                        output.add(table.frozenCards());
                        break;
                    case USEATTACK:
                        int attackerX = curr.getCardAttacker().getX();
                        int attackerY = curr.getCardAttacker().getY();
                        int attackedX = curr.getCardAttacked().getX();
                        int attackedY = curr.getCardAttacked().getY();
                        int error = table.cardAttack(attackerX, attackerY, attackedX, attackedY,
                                output);
                        if (error == 0) {
                            if (table.getGameTable()[attackerX][attackerY] != null) {
                                table.getGameTable()[attackerX][attackerY].setUsed(1);
                            }
                        }
                        break;
                    case USEABILITY:
                        int xAttacker = curr.getCardAttacker().getX();
                        int yAttacker = curr.getCardAttacker().getY();
                        int xAttacked = curr.getCardAttacked().getX();
                        int yAttacked = curr.getCardAttacked().getY();
                        error = table.cardAbility(xAttacker, yAttacker, xAttacked, yAttacked,
                                output);
                        if (error == 0) {
                            if (table.getGameTable()[xAttacker][yAttacker] != null) {
                                table.getGameTable()[xAttacker][yAttacker].setUsed(1);
                            }
                        }
                        break;
                    case HEROATTACK:
                        xAttacker = curr.getCardAttacker().getX();
                        yAttacker = curr.getCardAttacker().getY();
                        if (playerTurn == 1) {
                            error = table.attackHero(xAttacker, yAttacker, playerTurn,
                                    playerTwo.getHero(), output);
                            if (error == 0) {
                                if (table.getGameTable()[xAttacker][yAttacker] != null) {
                                    table.getGameTable()[xAttacker][yAttacker].setUsed(1);
                                }
                            } else if (error == 2) {
                                playerOneGamesWon++;
                                gamesPlayed++;
                                continue;
                            }
                        } else {
                            error = table.attackHero(xAttacker, yAttacker, playerTurn,
                                    playerOne.getHero(), output);
                            if (error == 0) {
                                if (table.getGameTable()[xAttacker][yAttacker] != null) {
                                    table.getGameTable()[xAttacker][yAttacker].setUsed(1);
                                }
                            } else if (error == 2) {
                                playerTwoGamesWon++;
                                gamesPlayed++;
                                continue;
                            }
                        }
                        break;
                    case HEROABILITY:
                        int row = curr.getAffectedRow();
                        if (playerTurn == 1) {
                            error = table.heroAbility(playerOne.getHero(), row,
                                    playerOne.getMana(), playerTurn, round, output);
                            if (error == 0) {
                                if (playerOne.getHero() != null) {
                                    playerOne.getHero().setUsed(1);
                                    playerOne.setMana(playerOne.getMana()
                                            - playerOne.getHero().getMana());
                                }
                            }
                        } else {
                            error = table.heroAbility(playerTwo.getHero(), row,
                                    playerTwo.getMana(), playerTurn, round, output);
                            if (error == 0) {
                                if (playerTwo.getHero() != null) {
                                    playerTwo.getHero().setUsed(1);
                                    playerTwo.setMana(playerTwo.getMana()
                                            - playerTwo.getHero().getMana());
                                }
                            }
                        }
                        break;
                    case ONEWINS:
                        objectNode.put("command", ONEWINS);
                        objectNode.put("output", playerOneGamesWon);
                        output.add(objectNode);
                        break;
                    case TWOWINS:
                        ObjectNode objectNode1 = mapper.createObjectNode();
                        objectNode1.put("command", TWOWINS);
                        objectNode1.put("output", playerTwoGamesWon);
                        output.add(objectNode1);
                        break;
                    case GAMES:
                        ObjectNode objectNode2 = mapper.createObjectNode();
                        objectNode2.put("command", GAMES);
                        objectNode2.put("output", gamesPlayed);
                        output.add(objectNode2);
                        break;
                    default:
                }
            }
        }
    }
}
