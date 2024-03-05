package nl.vu.kai.companion.restservice;

import nl.vu.kai.companion.Configuration.GardenConfigurationProperty;

public class PropertyResponse {

	private final GardenConfigurationProperty property;
	private final String result;
    private final String text;

	public PropertyResponse(GardenConfigurationProperty property, boolean res) {
		this.property = property;
		this.result = String.valueOf(res);
        if (res) {
            text = new String("Plants satisfy "+property.name+". " +property.description);
        } else
            text = new String("Plants do not satisfy "+property.name+". " +property.description.replace("contains", "does not contain"));
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