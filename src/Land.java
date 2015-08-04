public class Land extends Card {
	int mana;

	public Land(String name, int mana){
        super(CardType.Land);

		this.name = name;
		this.mana = mana;
		this.tapped = false;
	}

	public Land copy(){
		return new Land(name, mana);
	}
}