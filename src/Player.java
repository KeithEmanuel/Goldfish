import java.util.ArrayList;
import java.util.List;

public class Player {
    protected String name;
	protected int life;
	protected int mana;
	protected Deck deck;
	protected List<Card> hand;
	protected List<Creature> creatures;
	protected List<Land> lands;

	public Player(String name){
        this(name, new Deck(Deck.Option.Random));
	}

	public Player(String name, Deck deck){
		this.name = name;
		life = 20;
		mana = 0;
		this.deck = deck;
		hand = new ArrayList<>();
		creatures = new ArrayList<>();
		lands = new ArrayList<>();

		deck.shuffle();
		draw(7);
	}

	// returns true if the card is played, otherwise false
	public boolean play(Card card){
		if(mana >= card.cost) {
			Game.debug("Played " + card.name + " for " + card.cost + " mana.");

            card.tapped = false;

			hand.remove(card);
			mana -= card.cost;

			switch (card.type) {
				case Creature:
					playCreature((Creature) card);
					break;
				case Spell:
					playSpell((Spell) card);
					break;
				case Land:
					playLand((Land) card);
					break;
			}

			return true;
		}

		return false;
	}

	public boolean play(Card card, Enemy target){
		if(mana >= card.cost) {
			Game.debug("Played " + card.name + " for " + card.cost + " mana.");

			hand.remove(card);
			mana -= card.cost;

			switch (card.type) {
				case Creature:
					playCreature((Creature) card, target);
					break;
				case Spell:
					playSpell((Spell) card, target);
					break;
			}

			return true;
		}

		return false;
	}

	private void playLand(Land card){
		lands.add(card);
	}

	private void playSpell(Spell card){

	}

	private void playSpell(Spell card, Enemy target){
        target.life -= card.damage;
	}

	private void playCreature(Creature card){
		card.tapped = !card.hasAbility(Creature.Ability.Haste);
        creatures.add(card);
	}

	private void playCreature(Creature card, Enemy target){
        card.tapped = !card.hasAbility(Creature.Ability.Haste);
        creatures.add(card);
	}

    /**
     *
     * @return The card the was drawn.
     */
    public Card draw(){
        Card card = deck.draw();
        hand.add(card);
        return card;
    }

	public void draw(int n){
		for(int i = 0; i < n && deck.size() > 0; i++) {
			hand.add(deck.draw());
		}
	}

    public void upkeep(){

        for(Creature creature: creatures){
            if(creature.tapped)
                creature.tapped = false;
            if(creature.summoningSick)
                creature.summoningSick = false;
        }

        for(Land land: lands){
            land.tapped = false;
        }
    }

    public void endOfTurn(){
        this.mana = 0;
    }

    public int mulligan(){
        int handSize = hand.size();

        deck.addAll(hand);
        hand.clear();

        deck.shuffle();
        --handSize;

        draw(handSize);

        return handSize;
    }

	public void printDeck(){
		System.out.println("Player " + name + "'s deck has " + deck.size() + " cards:");
		System.out.println(deck.toString() + "\n");
	}

	public void printHand(){
		System.out.println("Player " + name + "'s hand has " + hand.size() + " cards:");
		System.out.println(hand.toString() + "\n");
	}
}