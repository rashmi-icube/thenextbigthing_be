package org.owen.filter;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owen.helper.DatabaseConnectionHelper;
import org.owen.helper.UtilHelper;

public class FilterHelper {

	/**
	 * Returns a filter object of the given filterName
	 * @param companyId - Company ID
	 * @param filterName - Name of the filter for which all values are to be returned
	 * @return filter object - A filter object of the given filterName
	 */
	public Filter getFilterValues(int companyId, String filterName) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		org.apache.log4j.Logger.getLogger(FilterHelper.class).debug("filterName : " + filterName);
		Filter f = new Filter();
		f.setFilterName(filterName);
		Map<Integer, String> filterLabelMap = getFilterLabelMap(companyId);
		for (int filterId : filterLabelMap.keySet()) {
			if (filterLabelMap.get(filterId).equalsIgnoreCase(filterName)) {
				f.setFilterId(filterId);
			}
		}
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getDimensionValue(?)}")) {
			cstmt.setInt(1, f.getFilterId());
			try (ResultSet rs = cstmt.executeQuery()) {
				Map<Integer, String> filterValuesMap = new HashMap<>();
				filterValuesMap.put(0, "All");
				while (rs.next()) {
					filterValuesMap.put(rs.getInt("dimension_val_id"), rs.getString("dimension_val_name"));
					f.setFilterValues(filterValuesMap);
				}
			}

		} catch (SQLException e) {
			org.apache.log4j.Logger.getLogger(FilterHelper.class).error("Exception in  getFilterValues for filter : " + filterName, e);

		}
		return f;
	}

	/**
	 * Retrieves all the objects which are filters
	 * @param companyId - Company ID
	 * @return a list of all filter objects
	 */
	public List<Filter> getFilterValues(int companyId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);

		List<Filter> allFiltersList = new ArrayList<>();

		Map<Integer, String> filterLabelMap = getFilterLabelMap(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getDimensionValueList()}");
				ResultSet rs = cstmt.executeQuery()) {

			for (int filterId : filterLabelMap.keySet()) {
				Filter f = new Filter();
				String filterName = filterLabelMap.get(filterId);

				f.setFilterId(filterId);
				f.setFilterName(filterName);
				org.apache.log4j.Logger.getLogger(FilterHelper.class).info("HashMap created!!!");
				Map<Integer, String> filterValuesMap = new HashMap<>();
				while (rs.next()) {
					if (filterId == rs.getInt("dimension_id")) {
						filterValuesMap.put(rs.getInt("dimension_val_id"), rs.getString("dimension_val_name"));
					}
				}
				f.setFilterValues(filterValuesMap);
				allFiltersList.add(f);
				rs.first();
			}

		} catch (SQLException e) {
			org.apache.log4j.Logger.getLogger(FilterHelper.class).error("Exception while getting dimension value list : ", e);
		}
		return allFiltersList;
	}

	/**
	 * Returns the map of filter labels
	 * @param companyId - Company ID
	 * @return filterLabelMap - A map of filter labels
	 */
	public Map<Integer, String> getFilterLabelMap(int companyId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		org.apache.log4j.Logger.getLogger(FilterHelper.class).info("HashMap created!!!");
		Map<Integer, String> filterLabelMap = new HashMap<>();
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getDimensionList()}");
				ResultSet rs = cstmt.executeQuery()) {
			while (rs.next()) {
				filterLabelMap.put(rs.getInt("dimension_id"), rs.getString("dimension_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return filterLabelMap;
	}

	public Map<Integer, String> getFilterValuesColors(int companyId) {

		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		Map<Integer, String> filterColorMap = new HashMap<>();
		List<Filter> filterLabelMap = getFilterValues(companyId);
		for (int i = 0; i < filterLabelMap.size(); i++) {
			Filter f = filterLabelMap.get(i);
			int colorIndex = 0;
			for (int filterValueId : f.getFilterValues().keySet()) {
				filterColorMap.put(filterValueId, UtilHelper.colorList.get(colorIndex));
				colorIndex++;
			}
		}
		return filterColorMap;
	}
}
