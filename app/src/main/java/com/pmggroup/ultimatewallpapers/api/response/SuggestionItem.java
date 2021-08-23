package com.pmggroup.ultimatewallpapers.api.response;

public class SuggestionItem {

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	private String suggestion;
	private boolean isSelected;

}