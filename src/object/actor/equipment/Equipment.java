package object.actor.equipment;

import java.util.ArrayList;
import java.util.List;

public class Equipment {
	private List<Equippable> eqList;
	private float attackBoost, defenseBoost;
	
	
	// Constructor
	
	public Equipment() {
		eqList = new ArrayList<Equippable>();
		
		attackBoost = 0;
		defenseBoost = 0;
	}
	
	
	
	// Getting Stats
	
	public float getAttack() {
		return attackBoost;
	}
	public float getDefense() {
		return defenseBoost;
	}
	
	
	
	// Equipping
	
	public void equip(Equippable e) {
		eqList.add(e);
		attackBoost += e.getAttack();
		defenseBoost += e.getDefense();
	}
	public void unequip(Equippable e) {	
		if(eqList.remove(e)) {
			attackBoost -= e.getAttack();
			defenseBoost -= e.getDefense();
		}
	}
	public void unequipAll() {
		eqList.clear();
		attackBoost = 0;
		defenseBoost = 0;
	}
}
