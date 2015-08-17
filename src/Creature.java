import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The class for Creature Cards.
 *
 */
public class Creature extends Card {

    enum Ability {
        Haste
    }

    List<Ability> abilities;
	int attack;
	int defense;
    boolean summoningSick;

    /**
     * Creates a new Creature with no abilities
     * @param name The card's name
     * @param cost The card's converted mana cost
     * @param attack The card's attack
     * @param defense The card's defense
     */
    public Creature(String name, int cost, int attack, int defense){
        super(CardType.Creature);

        this.abilities = new ArrayList<>();
        this.name = name;
        this.cost = cost;
        this.attack = attack;
        this.defense = defense;
        this.tapped = false;
        this.summoningSick = true;
    }

    /**
     * Creates a new Creature with abilities
     * @param name The card's name
     * @param cost The card's converted mana cost
     * @param attack The card's attack
     * @param defense The card's defense
     * @param ability A single creature ability
     */
    public Creature(String name, int cost, int attack, int defense, Ability ability){
        super(CardType.Creature);

        this.abilities = new ArrayList<>();
        this.abilities.add(ability);
        this.name = name;
        this.cost = cost;
        this.attack = attack;
        this.defense = defense;
        this.tapped = false;
        this.summoningSick = !hasAbility(Ability.Haste);
    }

    /**
     * Creates a new Creature with abilities
     * @param name The card's name
     * @param cost The card's converted mana cost
     * @param attack The card's attack
     * @param defense The card's defense
     * @param abilities A Collection of the creature's abilities
     */
	public Creature(String name, int cost, int attack, int defense, Collection<Ability> abilities){
        super(CardType.Creature);

        if(abilities == null){
            this.abilities = new ArrayList<>();
        }
        else{
            this.abilities = new ArrayList<>(abilities);
        }
		this.name = name;
		this.cost = cost;
		this.attack = attack;
		this.defense = defense;
		this.tapped = false;
        this.summoningSick = !hasAbility(Ability.Haste);
    }

    /**
     * Adds an ability to this creature
     * @param ability The ability to add
     */
    public void addAbility(Ability ability){
        abilities.add(ability);
    }

    /**
     * Removes an ability from this creature
     * @param ability The ability to remove
     */
    public void removeAbility(Ability ability){
        abilities.remove(ability);
    }

    /**
     * Checks to see if a creature has an ability
     * @param ability The ability to check
     * @return True if the creature has the ability, otherwise false
     */
    public boolean hasAbility(Ability ability){
        return abilities.contains(ability);
    }

    /**
     * Check if this creature can attack
     * @return True if the creature is able to attack, otherwise false
     */
    public boolean canAttack(){
        return !summoningSick && !tapped;
    }

    /**
     * Creates a new copy of this card
     * @return A copy of this card
     */
    public Creature copy(){
        return new Creature(name, cost, attack, defense, abilities);
    }
}
