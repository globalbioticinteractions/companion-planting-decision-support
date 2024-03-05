package nl.vu.kai.companion.restservice;

import java.util.List;
import java.util.Optional;

public class CompanionRequest {
	private final Optional<Boolean> intersectionOption; //true = intersection, false = union
	private final Optional<Boolean> companionOption; //true = companion of, false = anti companion of
	private final List<String> plantlist;
    
    public CompanionRequest( List<String> plants,Optional<Boolean> companion, Optional<Boolean> intersection) {
		this.intersectionOption = intersection;
		this.companionOption = companion;
		this.plantlist = plants;
	}

	public boolean getCompanionOption() {
		return companionOption.get();
	}

	public boolean getIntersectionOption() {
		return intersectionOption.get();
	}

	public List<String> getPlantlist() {
		return plantlist;
	}

    
}
