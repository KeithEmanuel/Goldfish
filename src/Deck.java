import java.util.*;


/**
 * The Deck class is mostly a wrapper for a List of Cards.
 *
 * The originalDeck variable is used to convert a deck back to it's original state, since cards will be removed from it.
 */
public class Deck {

	enum Option {
		Random
	}

	private List<Card> originalDeck;
	protected List<Card> deck;
    protected CardCatalog cardCatalog;

    /**
     * Creates a new, empty deck.
     */
	public Deck(){
		deck = new ArrayList<>();
	}

    /**
     * Creates a new Deck with an option. Currently, the only option is Option.Random.
     * @param option The Option used to create the deck.
     */
	public Deck(Option option){
		generateDeck(option);
	}

    /**
     * Creates a deck using an Option and a CardCatalog.
     * @param option The Option used to create the deck.
     * @param cardCatalog The catalog of cards used in the creation of the deck.
     */
    public Deck(Option option, CardCatalog cardCatalog){

        this.cardCatalog = cardCatalog;
        generateDeck(option);
    }

    /**
     * Creates a new Deck object using an existing deck of cards.
     * @param deck The Deck that the cards will be copied from.
     */
    public Deck(Deck deck){
        this.deck = deck.deck;
        save();
    }

    /**
     * Creates a new Deck object using a List of Card objects for the deck.
     * @param deck The List of Cards that will be used as the deck.
     */
    public Deck(List<Card> deck){
        this.deck = deck;
        save();
    }

    /**
     * Takes an Option and uses the rules of that option to create the deck.
     * @param option The Option used to create the deck.
     */
    private void generateDeck(Option option){
        if(cardCatalog == null){
            cardCatalog = new CardCatalog();
        }

        if(option == Option.Random) {
            deck = new ArrayList<>();

            for (int i = 0; i < 15 + cardCatalog.rand.nextInt(10); i++) {
                deck.add(cardCatalog.getRandomLand());
            }

            int landCount = deck.size();

            for (int i = 0; i < 60 - landCount; i++) {
                deck.add(cardCatalog.getRandomNonland());
            }

            Collections.shuffle(deck);
            save();
        }
    }

	public int size(){
		return deck.size();
	}

	public Card draw(){
		return deck.remove(0);
	}

	public Card get(int n){
		return deck.get(n);
	}

	public void add(Card card){
		deck.add(card);
	}

	public void add(Card card, int count){
		for(int i = 0; i < count; i++){
			deck.add(card.copy());
		}
	}

    public void addAll(Collection<Card> cards){
        deck.addAll(cards);
    }

	public void shuffle(){
		Collections.shuffle(deck);
	}

	private List<Card> getCardList(){
		return deck;
	}

    /**
     * Sorts the deck, placing lands before other cards, and sorting by name.
     */
    public void sort(){
        deck.sort(new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                if(o1.type == Card.CardType.Land && o2.type != Card.CardType.Land){
                    return -1;
                }
                else if(o2.type == Card.CardType.Land && o1.type != Card.CardType.Land){
                    return 1;
                }
                else{
                    return o1.name.compareTo(o2.name);
                }
            }
        });
    }

    /**
     * Creates a new copy of this Deck.
     * @return A new copy of this Deck.
     */
	public Deck copy(){
		Deck copy = new Deck(new ArrayList<>(deck));
		copy.save();

		return copy;
	}

    public List<Card> subList(int min, int max){
        return deck.subList(min, max);
    }

    /**
     * Replaces a Card at a position with a new Card
     * @param pos The index of the card to replace.
     * @param newCard The card that will be used to replace the old card.
     */
    public void replace(int pos, Card newCard){
        deck.remove(pos);
        deck.add(newCard);
    }

    /**
     * reset will revert the deck back to the state that it was in when save() was last called, as well as uptap
     * each card in the deck.
     */
	public void reset(){
        for(Card c : originalDeck){
            if(c.tapped){
                c.tapped = false;
            }
        }

		deck = new ArrayList<>(originalDeck);
	}

    /**
     * Save is used to set the deck this Deck will revert to when reset is called.
     */
	public void save(){
		originalDeck = new ArrayList<>(deck);
	}


    public String toString(){
        String str = "Size: " + deck.size() + "\n";
        str += "-- Deck List --\n";
        HashMap<String, Integer> hashMap = new HashMap<>();

        for(Card card : deck){
            if(!hashMap.containsKey(card.name)){
                hashMap.put(card.name, 1);
            }
            else {
                hashMap.put(card.name, hashMap.get(card.name) + 1);
            }
        }

        Set<String> set = hashMap.keySet();

        for(String key : set){
            str += key + ": " + hashMap.get(key) + "\n";
        }

        return str;
    }
}