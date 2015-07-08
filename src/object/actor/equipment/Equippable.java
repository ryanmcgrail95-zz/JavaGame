package object.actor.equipment;

public class Equippable {
	private String name;
	private float attack, defense;

	
	// Constructor
	
	public Equippable(String name, float attack, float defense) {
		this.name = name;
		this.attack = attack;
		this.defense = defense;
	}
	
	
	
	// Getting Stats
	
	public String getName() {
		return name;
	}
	public float getAttack() {
		return attack;
	}
	public float getDefense() {
		return defense;
	}
}
