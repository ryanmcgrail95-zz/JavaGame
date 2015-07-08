package obj.prt;

public class Spark extends Particle {
	private double direction;
	
	public Spark(double x, double y, double z, double direction) {
		super(x,y,z);
	
		this.direction = direction;
	}
}
