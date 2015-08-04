/**
 * Card is the superclass of all cards.
 * Subclasses are card types, such as Creature, Land, or Spell.
 *
 * Currently, cost is just the converted mana cost of a card, and any land can produce mana for any card.
 * Implementing mana costs to allow for more complex mana bases is on my to do list.
 *
 * Also, Card should probably be final, not abstract, since cards can gain/lose card types in magic.
 */

public abstract class Card {

    enum CardType {
        Creature,
        Spell,
        Land
    }

	String name;
	final CardType type;
	int cost;
	boolean tapped;

    protected Card(CardType type){
        this.type = type;
    }

	public String toString(){
		return name;
	}

	public abstract Card copy();
}