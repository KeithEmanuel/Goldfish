public class Spell extends Card {
	int damage;

	public Spell(String name, int cost, int damage) {
        super(CardType.Spell);

		this.name = name;
		this.cost = cost;
		this.damage = damage;
		this.tapped = false;
	}

	public Spell copy(){
		return new Spell(name, cost, damage);
	}
}
