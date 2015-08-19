import sun.font.CreatedFontTracker;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * CardCatalog is used to hold the available card objects and create decks.
 *
 * Usage:
 * Most of the time, you will just need to call the constructor of this class. The constructor takes a
 * Collection of cards or a string array of card names. The default constructor creates a catalog of all cards.
 * Only cards in the catalog will be returned by methods in this catalog.
 *
 * todo: modify (along with the Card class and subclasses) to use JSON card data.
 */
public class CardCatalog {

	public final Random rand = new Random();
	private final List<Card> cards;
    private final List<Card> lands;
	private final List<Card> nonlands;

    // All card data is stored in objects here.
	private static List<Card> allCards = Arrays.asList(

			/***** Creatures *****/
            new Creature(
                    "Goblin Guide",
                    1,
                    2,
                    2,
                    Arrays.asList(Creature.Ability.Haste)
            ),

            new Creature(
                    "Mon's Goblin Raiders",
                    1,
                    1,
                    1
            ),

			new Creature(
					"Jackal Pup",
					1,  // cost
					2,  // attack
					1   // defense
			),

            new Creature(
                    "Grizzly Bears",
                    2,
                    2,
                    2
            ),

			new Creature(
					"Mogg Flunkies",
					2,
					3,
					3
			),

            new Creature(
                    "Elephant",
                    3,
                    3,
                    3
            ),

			new Creature(
					"Hill Giant",
					4,
					3,
					3
			),

            new Creature(
                    "1cmc 1/1",
                    1,
                    1,
                    1
            ),

            new Creature(
                    "2cmc 2/2",
                    2,
                    2,
                    2
            ),

            new Creature(
                    "3cmc 3/3",
                    3,
                    3,
                    3
            ),

            new Creature(
                    "4cmc 4/4",
                    4,
                    4,
                    4
            ),

			/***** Spells *****/
			new Spell(
					"Lightning Bolt",
					1,  // cost
					3   // damage
			),

			new Spell(
					"Shock",
					1,
					2
			),

            new Spell(
                    "Incinerate",
                    2,
                    3
            ),

			new Spell(
					"Flame Rift",
					2,
					4
			),


			/***** Lands *****/
			new Land(
					"Mountain",
					1   // mana
			)
	);

    /**
     * Default constructor which creates a CardCatalog containing all cards.
     */
    public CardCatalog(){
        cards = allCards;
        lands = cards.stream().filter(c -> c.type == Card.CardType.Land).collect(Collectors.toList());
        nonlands = cards.stream().filter(c -> c.type != Card.CardType.Land).collect(Collectors.toList());
    }

    /**
     * This constructor takes a list of cards and creates a CardCatalog object using only cards in that list.
     * @param cards the collection of cards to use in the catalog.
     */
    public CardCatalog(Collection<Card> cards){
        this.cards = new ArrayList<>(cards);
        lands = cards.stream().filter(c -> c.type == Card.CardType.Land).collect(Collectors.toList());
        nonlands = cards.stream().filter(c -> c.type != Card.CardType.Land).collect(Collectors.toList());
    }


    /**
     * Similar to the Collection<Card> constructor, but using a String[] of card names.
     * @param cardNames An array containing the names of cards to use in the deck.
     */
    public CardCatalog(String[] cardNames){

        this.cards = allCards.stream().filter((o) -> Arrays.asList(cardNames).contains(o.name)).collect(Collectors.toList());

        // make sure that all of the cards are valid
        for(String name : cardNames){
            getCard(name);
        }

        lands = cards.stream().filter(c -> c.type == Card.CardType.Land).collect(Collectors.toList());
        nonlands = cards.stream().filter(c -> c.type != Card.CardType.Land).collect(Collectors.toList());
    }

    /**
     * Adds a card to the CardCatalog
     * @param card The card to add.
     */
    public void addCard(Card card){
        cards.add(card);
    }

    /**
     * Adds a card, by name, to the catalog
     * @param name The name of the card to add.
     */
    public void addCard(String name){
        cards.add(getCard(name));
    }

    public void removeCard(Card card){
        cards.remove(card);
    }

    public void removeCard(String name){
        for(Card card : cards){
            if(card.name.equals(name)){
                cards.remove(card);
            }
        }
    }

    /**
     * Gets the cards available in the catalog as a list
     * @return A list of cards in the catalog.
     */
	public List<Card> getCardList(){
		return cards;
	}

    /**
     * @return The number of cards in the catalog.
     */
	public int getCatalogSize(){
		return cards.size();
	};

    /**
     * Gets a copy of a card from the catalog
     * @param name The name of the card to return
     * @return A new Card object
     */
	public Card getCard(String name){
        return cards.stream()
                .filter((c) -> c.name.equals(name))
                .findAny()
                .orElseThrow(() -> new InvalidParameterException("No card named '" + name + "'"))
                .copy();
	}

    /**
     * Get a random card from the catalog.
     * @return A copy of a random Card object
     */
	public Card getRandomCard(){
		return cards.get(rand.nextInt(cards.size())).copy();
	}

    /**
     * Get a random card from a list of supplied cards
     * @param possibleCards A list of possible cards to return
     * @return A copy of a random card in the supplied list
     */
    public Card getRandomCard(List<Card> possibleCards){
        return possibleCards.get(rand.nextInt(cards.size())).copy();
    }

    /**
     * Get a random land card
     * @return A copy of a random land card
     */
	public Card getRandomLand(){
		return lands.get(rand.nextInt(lands.size())).copy();
	}

    /**
     * Get a random non-land card
     * @return A copy of a random non-land card
     */
	public Card getRandomNonland(){
		return nonlands.get(rand.nextInt(nonlands.size())).copy();
	}
}
