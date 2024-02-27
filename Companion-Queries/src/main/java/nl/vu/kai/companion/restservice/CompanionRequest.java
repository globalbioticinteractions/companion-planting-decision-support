package nl.vu.kai.companion.restservice;

import java.util.List;

public class CompanionRequest {
	private final boolean intersectionOption; //true = intersection, false = union
	private final boolean companionOption; //true = companion of, false = anti companion of
	private final List<String> plantlist;
    
    public CompanionRequest(boolean companion, boolean intersection, List<String> plants) {
		this.intersectionOption = intersection;
		this.companionOption = companion;
		this.plantlist = plants;
	}

	public boolean getCompanionOption() {
		return companionOption;
	}

	public boolean getIntersectionOption() {
		return intersectionOption;
	}

	public List<String> getPlantlist() {
		return plantlist;
	}

    
}
