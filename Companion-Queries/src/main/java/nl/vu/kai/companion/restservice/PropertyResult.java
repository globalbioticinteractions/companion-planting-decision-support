package nl.vu.kai.companion.restservice;

import nl.vu.kai.companion.Configuration.GardenConfigurationProperty;

public class PropertyResult {

	private final GardenConfigurationProperty property;
	private final String result;
    private final String text;

	public PropertyResult(GardenConfigurationProperty property, boolean res) {
		this.property = property;
		this.result = String.valueOf(res);
        if (res) {
            text = new String("Plants satisfy "+property.name+".");
        } else
            text = new String("Plants do not satisfy "+property.name+".");
	}

	public GardenConfigurationProperty getProperty() {
		return property;
	}

	public String getResult() {
		return result;
	}

    public String getText() {
		return text;
	}
}