package paper;

public class Attack {
	public final static byte
		AT_JUMP = 0,
		AT_HAMMER = 1;
	
	private String name;
	private int attackPower;
	private byte
		attackElement,
		attackType;
	
	public Attack(String name, int power, byte atType, byte el) {
		this.name = name;
		this.attackPower = power;
		this.attackElement = el;
		this.attackType = atType;
	}
	
	public String getName() {return name;}
	public int getAttack() 	{return attackPower;}
	public byte getElement() {return attackElement;}
	public byte getAttackType() {return attackType;}
}
