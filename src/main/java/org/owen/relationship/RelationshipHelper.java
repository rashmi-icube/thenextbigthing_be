package org.owen.relationship;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owen.helper.DatabaseConnectionHelper;

public class RelationshipHelper {

	/**
	 * Returns a filter object of the given filterName
	 * @param companyId - Company ID
	 * @param filterName - Name of the filter for which all values are to be returned
	 * @return filter object - A filter object of the given filterName
	 */
	public Map<Integer, List<Relationship>> getRelationshipValues(int companyId) {
		Map<Integer, List<Relationship>> result = new HashMap<>();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getRelationshipValue()}");
				ResultSet rs = cstmt.executeQuery()) {
			while (rs.next()) {
				int relId = rs.getInt("rel_type_id");
				if (result.containsKey(relId)) {
					List<Relationship> list = result.get(relId);
					Relationship r = new Relationship();
					r.setRelationshipTypeId(rs.getInt("rel_type_id"));
					r.setRelationshipTypeName(rs.getString("rel_type_name"));
					r.setRelationshipId(rs.getInt("rel_id"));
					r.setRelationshipName(rs.getString("rel_name"));
					list.add(r);
					result.put(relId, list);
				} else {
					List<Relationship> list = new ArrayList<>();
					Relationship r = new Relationship();
					r.setRelationshipTypeId(rs.getInt("rel_type_id"));
					r.setRelationshipTypeName(rs.getString("rel_type_name"));
					r.setRelationshipId(rs.getInt("rel_id"));
					r.setRelationshipName(rs.getString("rel_name"));
					list.add(r);
					result.put(relId, list);
				}
			}
		} catch (SQLException e) {
			org.apache.log4j.Logger.getLogger(RelationshipHelper.class).error("Exception in  getRelationshipValues ", e);

		}
		return result;
	}
}
