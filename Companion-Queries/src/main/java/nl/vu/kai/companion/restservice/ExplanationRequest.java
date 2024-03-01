package nl.vu.kai.companion.restservice;

import java.util.List;

import nl.vu.kai.companion.Configuration.GardenConfigurationProperty;

public class ExplanationRequest {
	private final GardenConfigurationProperty property;
	private final List<String> plantlist;
    
    public ExplanationRequest(GardenConfigurationProperty prop, List<String> plants) {
		this.property = prop;
		this.plantlist = plants;
	}

	public GardenConfigurationProperty getProperty() {
		return property;
	}

	public List<String> getPlantlist() {
		return plantlist;
	}

    
}
