package org.owen.filter;

import java.util.Map;

public class Filter{
	private String filterName;
	private int filterId;
	private Map<Integer, String> filterValues;

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public Map<Integer, String> getFilterValues() {
		return filterValues;
	}

	public void setFilterValues(Map<Integer, String> filterValuesMap) {
		this.filterValues = filterValuesMap;
	}

	public int getFilterId() {
		return filterId;
	}

	public void setFilterId(int filterId) {
		this.filterId = filterId;
	}

}
