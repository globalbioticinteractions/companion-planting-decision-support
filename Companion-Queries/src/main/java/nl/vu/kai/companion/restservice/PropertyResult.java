package nl.vu.kai.companion.restservice;

import nl.vu.kai.companion.Configuration.GardenConfigurationProperty;

public class PropertyResult {

	private final GardenConfigurationProperty property;
	private final String content;

	public PropertyResult(GardenConfigurationProperty property, boolean res) {
		this.property = property;
		this.content = String.valueOf(res);
	}

	public GardenConfigurationProperty getProperty() {
		return property;
	}

	public String getContent() {
		return content;
	}
}