package menu;

public class PartnerList extends ListMenu<PartnerItem> {

	public PartnerList(float x, float y) {
		super(x, y);
		
		add(new PartnerItem("Luigi"));
	}
}
