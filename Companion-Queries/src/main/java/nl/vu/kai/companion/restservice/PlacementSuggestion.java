package nl.vu.kai.companion.restservice;

import nl.vu.kai.companion.Configuration.GardenConfigurationProperty;

public class PlacementSuggestion {
    private final String subject;
	private final String property;
    private final String object;

	public PlacementSuggestion(String s, String p, String o) {
		this.subject = s;
        this.property = p;
        this.object = o;
	}
    public PlacementSuggestion(String spo) {
		String[] ax = spo.split(" ");
        this.subject = ax[0];
        this.property = ax[1];
        this.object = ax[2];
	}

	public String getProperty() {
		return property;
	}

	public String getSubject() {
		return subject;
	}

    public String getObject() {
		return object;
	}
}
