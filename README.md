**Petrache Gabriela**

## GwentStone

### Description:

* Implemented a card game in Java, that is a combination of the already existing Hearthstone and Gwent games, using basic OOP concepts
* Two players duel with the purpose of destroying each other's Hero character and winning the game
* Each player has their own deck of cards, with different attacks and abilities, that they can use to attack the enemy's cards
* The first player to kill the enemy's Hero card wins the game

### Implementation:

* After parsing and organizing the input data from the JSON files,
I have created the following objects to help implement my game:
    * a package of cards, which contains all types of cards used for this game, that inherit the main card class
    * each type of card has its own set of abilities and its own position during the game
    * most of the action happens on the game table, where cards use their special abilities and attacks to destroy the enemy's set
* The cards, depending on their type and the commands given as an input, can be either stored in the player's
deck, placed in their hand, or used on the game table
  * Environment cards are stored in the player's hand and they affect an entire row when placed on the table. There are three types of Environment cards implemented
  * Minion cards have the ability to protect their own rows on the table, or attack the enemy's cards
  * Each player gets one Hero card at the beginning of the game, and the whole purpose of the game is for each player to protect their own hero, and to defeat the enemy's. Heroes also have abilities that they can useto benefit their rows or to attack the enemy's rows
* The places where cards are stored (hands, table and decks) interact with each other based on the commands given as an input
* A lot of checks happen inside the code, because each type of card has their fixed abilities, depending on their names and types
* Cards use different types of methods and classes to apply their abilities, depending on the input from each command and possible errors
* To avoid duplicated code as much as possible I have created some helper Output Printer functions, along with some strings, which can be found in the utils package
* Doing this assignment, I have learnt a lot about working with classes and instances in Java, along with OOP principles
