package animation;

public interface Interpolatable {
	public void updateValues(Interpolatable other);
	public Interpolatable tween(Interpolatable toValue, float amt);
}