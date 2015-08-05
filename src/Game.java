 import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

 /***
  * Game contains the logic used when played a "Goldfish" game, or a game with an enemy that is nothing more than a life
  * total.
  *
  * Currently, mana produced in a turn is equal to the count of lands. So, any land will produce one mana of any color.
  * This is because of two things: It is faster, and it currently makes no difference in the program execution.
  *
  * The debug calls to toString() can be expensive, so they are currently commented out.
  * todo: implement a better debug system
  */
public class Game {
    final private static Logger log = LoggerFactory.getLogger(Game.class);
    final public static boolean DEBUG = false;

	Player player;
	Enemy enemy;
	int turn;

     /**
      * Creates a new Game with a Player using a random deck.
      */
	public Game(){
		player = new Player("Good");
		enemy = new Enemy();
		turn = 0;
	}

     /**
      * Creates a new Game with a Player using a specific deck.
      * @param deck The Deck used by the player.
      */
	public Game(Deck deck){
        deck.reset();
		player = new Player("Good", deck.copy());
		enemy = new Enemy();
		turn = 0;
	}

     /**
      * Plays an entire game of Magic, and returns the turn the game finished.
      * @return The turn the the player won on.
      */
	public int playGame(){

        // mulligan logic
        int landsInHand = player.hand.stream()
                .filter((c) -> c.type == Card.CardType.Land)
                .collect(Collectors.toList())
                .size();

        while (player.hand.size() > 5 && (landsInHand >= 5 || landsInHand <= 1)){
            player.mulligan();
            landsInHand = player.hand.stream()
                    .filter((c) -> c.type == Card.CardType.Land)
                    .collect(Collectors.toList())
                    .size();
        }

        // play till the win
		while(enemy.life > 0){

			player.deck.shuffle();

			// if the deck sucks, return early
			if(turn >= 12){
				return turn;
			}

			playTurn();
		}

        // called to set all the cards to untapped. todo: refactor
        player.deck.reset();

		return turn;
	}

     /**
      * The logic used to play a single turn of Magic.
      */
	private void playTurn(){
		upkeep();

		// play a (random) land for the turn
        Optional<Card> landToPlay = player.hand.stream().filter((c) -> c.type == Card.CardType.Land).findFirst();
        if(landToPlay.isPresent()) {
            player.play(landToPlay.get());
        }

		// add all mana we have available this turn to our mana pool (simplified)
		player.mana = player.lands.size();

        List<Card> cardsToPlay = findBestPlay();

        for(Card card : cardsToPlay){
            if(card.type == Card.CardType.Creature) {
                player.play(card);
            }
            else if(card.type == Card.CardType.Spell) {
                player.play(card, enemy);
            }
        }

		attack();

		endOfTurn();
	}

     /**
      * The 'upkeep' phase of magic, except that it contain pretty much everything before the first main phase.
      */
	private void upkeep(){
		turn++;
		player.upkeep();

        // commented out for performance
		//debug("\nTurn " + turn + ":");
		//debug("Hand: " + player.hand.toString());
		//debug("Creatures: " + player.creatures.toString());
		//debug("Lands: " + player.lands.toString());
	}

     /**
      * The 'End of turn' phase of magic.
      */
	private void endOfTurn(){
		player.endOfTurn();
		//debug("Enemy health remaining: " + enemy.life);
	}

     /**
      * Attacks with every creature that can attack.
      */
	private void attack(){
		int damage = 0;

        //List<Creature> attackingCreatures = player.creatures.stream().filter((c) -> c.canAttack()).collect(Collectors.toList());

		//for(Creature creature : attackingCreatures){

        for(int i = 0; i < player.creatures.size(); i++){
            if(player.creatures.get(i).canAttack()) {

                //debugplayer.creatures.get(i).name + " is attacking.");
                damage += player.creatures.get(i).attack;
                player.creatures.get(i).tapped = true;

            }
		}

		enemy.life -= damage;
		//debug"Attacked for " + damage + " damage.");
	}

     /**
      * Finds the best combination of cards to play for the turn. Currently the second most expensive function in
      * the program.
      * @return A list containing the cards determined to be the best play.
      */
    private List<Card> findBestPlay(){
        List<Card> nonlands = player.hand.stream()
                .filter((c) -> c.type != Card.CardType.Land)
                .filter((c) -> c.cost <= player.mana)
                .collect(Collectors.toList());

        Set<PossiblePlay> possiblePlays = constructPossiblePlays(new PossiblePlay(nonlands));


        possiblePlays = possiblePlays.stream()
                .filter((p) -> p.getTotalCost() <= player.mana)
                .collect(Collectors.toSet());

        //debug("Possible plays: " + possiblePlays.toString());

        int totalCreatureAttack = 0;

        for(Creature c : player.creatures){
            if(c.canAttack()){
                totalCreatureAttack += c.attack;
            }
        }

        // killPlay does the most damage this turn, used if we can get the kill this turn
        PossiblePlay killPlay = possiblePlays.stream()
                .max((o1, o2) -> Double.compare(o1.getTurnPlayRank(), o2.getTurnPlayRank())).get();

        // overallPlay invests in recurring attack damage, so there is less damage this turn, but more next turn
        PossiblePlay overallPlay = possiblePlays.stream()
                .max((o1, o2) -> Double.compare(o1.getOverallPlayRank(), o2.getOverallPlayRank())).get();

        if(enemy.life - killPlay.getTurnDamage() - totalCreatureAttack <= 0){
            return killPlay.toList();
        }

        return overallPlay.toList();
    }


     /**
      * Constucts a power set of PossiblePlay objects using a PossiblePlay object containing all of the playable
      *     cards that the player has in their hand.
      *     This is the most expensive function in the program. todo: speed up.
      * @param originalCards The players hand, used to create the power set.
      * @return The power set of the players hand -- all possible plays that the player can make, not taking mana in
      *     to account.
      */
    private Set<PossiblePlay> constructPossiblePlays(PossiblePlay originalCards){
        Set<PossiblePlay> possiblePlays = new HashSet<>();

        if(originalCards.isEmpty()){
            possiblePlays.add(new PossiblePlay());
            return possiblePlays;
        }

        List<Card> list = new ArrayList<>(originalCards.toList());
        Card head = list.get(0);
        PossiblePlay rest = new PossiblePlay(list.subList(1, list.size()));
        for(PossiblePlay possiblePlay : constructPossiblePlays(rest)){
            PossiblePlay newPlay = new PossiblePlay();
            newPlay.addCard(head);
            newPlay.addCards(possiblePlay);
            possiblePlays.add(newPlay);
            possiblePlays.add(possiblePlay);

        }

        return possiblePlays;
    }
    
    public static void debug(String str){
        if(DEBUG){
            System.out.println(str);
        }
    }

}


/**
 * PossiblePlay contains a list of cards that can be played for the current turn, and functions to evaluate the play.
 */
class PossiblePlay {

    List<Card> cards;
    private boolean turnDamageCached;
    private int turnDamage;
    private boolean totalAttackCached;
    private int totalAttack;
    private boolean totalCostCached;
    private int totalCost;

    /**
     * Creates a new, empty, PossiblePlay.
     */
    public PossiblePlay(){
        cards = new ArrayList<>();
    }

    /**
     * Creates a new PossiblePlay using the specified cards.
     * @param cards The playable cards to evaluate
     */
    public PossiblePlay(List<Card> cards){
        this.cards = new ArrayList<>(cards);
    }

    /**
     * Invalidates the cached results of certain functions. Call when the list of cards is modified.
     */
    void invalidateCache(){
        if(turnDamageCached) turnDamageCached = false;
        if(totalAttackCached) totalAttackCached = false;
        if(totalCostCached) totalCostCached = false;
    }
    
    void addCard(Card c){
        cards.add(c);
        invalidateCache();
    }

    void addCards(Collection<Card> cardCollection){
        cards.addAll(cardCollection);
        invalidateCache();
    }
    
    void addCards(PossiblePlay playCards){
        cards.addAll(playCards.cards);
        invalidateCache();
    }

    boolean removeCard(Card card){
        return cards.remove(card);
    }

    Card removeCard(int index){
        return cards.remove(index);
    }

    /**
     * A play ranking function that prioritizes long-term damage.
     * @return A higher-is-better rank of the play.
     */
    double getOverallPlayRank(){
        return getTotalAttack() * 100 + getTurnDamage();
    }

    /***
     * A play ranking function that prioritizes damage that can be dealt on the current turn.
     * @return A higher-is-better rank of the play.
     */
    double getTurnPlayRank(){
        return getTurnDamage() * 100 + getTotalAttack();
    }

    /**
     * @return The amount of damage that this play will do on the current turn.
     */
    int getTurnDamage(){
        if(!turnDamageCached) {
            int damage = 0;

            for (Card card : cards) {
                if (card.type == Card.CardType.Spell) {
                    damage += ((Spell) card).damage;
                } else if (card.type == Card.CardType.Creature
                        && ((Creature) card).hasAbility(Creature.Ability.Haste)) {
                    damage += ((Creature) card).attack;
                }
            }

            turnDamage = damage;
            turnDamageCached = true;
        }
        
        return turnDamage;
    }

    /**
     * @return The amount of attack power that will be added to the board for this play.
     */
    int getTotalAttack(){
        if(!totalAttackCached) {
            int totalAttack = 0;

            for (Card card : cards) {
                if (card.type == Card.CardType.Creature) {
                    totalAttack += ((Creature) card).attack;
                }
            }
            
            this.totalAttack = totalAttack;
            totalAttackCached = true;
        }

        return this.totalAttack;
    }

    /**
     * @return The total converted mana cost of the cards in this play.
     */
    int getTotalCost(){
        if(!totalCostCached) {
            int totalCost = 0;

            for (Card c : cards) {
                totalCost += c.cost;
            }
            
            this.totalCost = totalCost;
        }
        
        return totalCost;
    }

    List<Card> subList(int min, int max){
        return cards.subList(min, max);
    }

    List<Card> toList(){
        return cards;
    }

    int size(){
        return cards.size();
    }

    boolean isEmpty(){
        return cards.isEmpty();
    }

    public  String toString(){
        return cards.toString();
    }
}