package org.owen.hr;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owen.helper.DatabaseConnectionHelper;

public class HrDashboardHelper {

	public static void main(String arg[]) {
		HrDashboardHelper hr = new HrDashboardHelper();
		System.out.println(hr.getSelfPerception(1, 1, 18, 19));
		
	}

	public String getNodeList(int companyId, int functionId, int positionId, int locationId) {
		JSONArray nodeList = new JSONArray();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);

		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getNodeList(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);
			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {

					JSONObject node = new JSONObject();
					node.put("id", res.getInt("emp_id"));
					node.put("firstName", res.getString("first_name"));
					node.put("lastName", res.getString("last_name"));
					nodeList.put(node);
					/*Node n = new Node();
					n.setEmployeeId(res.getInt("emp_id"));
					n.setFirstName(res.getString("first_name"));
					n.setLastName(res.getString("last_name"));				
					nodeList.add(n);*/
				}
				Logger.getLogger(HrDashboardHelper.class).debug("Node list size : " + nodeList.length());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving team networks diagram nodes ", e1);
		}
		return nodeList.toString();

	}

	public String getEdgeList(int companyId, int functionId, int positionId, int locationId) {
		JSONArray edgeList = new JSONArray();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getEdgeList(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);
			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {

					JSONObject e = new JSONObject();
					e.put("from", res.getInt("from_id"));
					e.put("to", res.getInt("to_id"));
					e.put("relId", res.getInt("rel_id"));
					e.put("weight", res.getDouble("weight"));
					edgeList.put(e);

					/*Edge e = new Edge();
					e.setFromEmployeeId(res.getInt("from_id"));
					e.setToEmployeeId(res.getInt("to_id"));
					e.setRelationshipTypeId(res.getInt("rel_id"));
					e.setWeight(res.getDouble("weight"));
					edgeList.add(e);*/
				}
			}

			Logger.getLogger(HrDashboardHelper.class).debug("Edge list size : " + edgeList.length());
		} catch (SQLException | JSONException e) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving team networks diagram edges ", e);
		}
		return edgeList.toString();
	}

	public String getRelIndexValue(int companyId, int functionId, int positionId, int locationId) {
		JSONArray result = new JSONArray();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getRelIndexValue(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);
			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					JSONObject json = new JSONObject();
					json.put("relId", res.getInt("rel_id"));
					json.put("indexValue", res.getDouble("metric_value"));
					json.put("explanation", res.getString("explanation"));
					json.put("action", res.getString("action"));
					json.put("responseCount", res.getInt("response_count"));
					result.put(json);
				}
			} catch (JSONException e) {
				Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving index value", e);

			}
		} catch (SQLException e1) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving index value", e1);
		}
		return result.toString();
	}

	public String getRelKeyPeople(int companyId, int functionId, int positionId, int locationId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		JSONArray result = new JSONArray();
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getRelKeyPeople(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);
			Map<Double, String> rel1 = new TreeMap<>();
			Map<Double, String> rel2 = new TreeMap<>();
			Map<Double, String> rel3 = new TreeMap<>();
			Map<Double, String> rel4 = new TreeMap<>();
			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					if (res.getInt("rel_id") == 1) {
						rel1.put(res.getDouble("emp_rank"), res.getString("first_name") + " " + res.getString("last_name"));
					} else if (res.getInt("rel_id") == 2) {
						rel2.put(res.getDouble("emp_rank"), res.getString("first_name") + " " + res.getString("last_name"));
					} else if (res.getInt("rel_id") == 3) {
						rel3.put(res.getDouble("emp_rank"), res.getString("first_name") + " " + res.getString("last_name"));
					} else if (res.getInt("rel_id") == 4) {
						rel4.put(res.getDouble("emp_rank"), res.getString("first_name") + " " + res.getString("last_name"));
					}

					// JSONObject json = new JSONObject();
					// json.put("relId", res.getInt("rel_id"));
					// json.put("empId", res.getInt("emp_id"));
					// json.put("firstName", res.getString("first_name"));
					// json.put("lastName", res.getString("last_name"));
					// json.put("rank", res.getInt("emp_rank"));
					// result.put(json);
				}

				JSONObject json1 = new JSONObject();
				json1.put("relId", 1);
				json1.put("keyPeople", rel1.values());
				result.put(json1);

				JSONObject json2 = new JSONObject();
				json2.put("relId", 2);
				json2.put("keyPeople", rel2.values());
				result.put(json2);

				JSONObject json3 = new JSONObject();
				json3.put("relId", 3);
				json3.put("keyPeople", rel3.values());
				result.put(json3);

				JSONObject json4 = new JSONObject();
				json4.put("relId", 4);
				json4.put("keyPeople", rel4.values());
				result.put(json4);

			}
		} catch (JSONException | SQLException e) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving key people", e);
		}
		return result.toString();
	}

	public String getWordCloud(int companyId, int functionId, int positionId, int locationId) {
		JSONArray result = new JSONArray();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getWordCloud(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);

			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					JSONObject json = new JSONObject();
					json.put("relId", res.getInt("rel_id"));
					json.put("word", res.getString("word"));
					json.put("weight", res.getInt("weight"));
					result.put(json);
				}
			}
		} catch (JSONException | SQLException e) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving word cloud", e);
		}
		return result.toString();
	}

	public String getSentimentScore(int companyId, int functionId, int positionId, int locationId) {
		JSONArray result = new JSONArray();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getSentimentScore(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);

			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					JSONObject json = new JSONObject();
					json.put("relId", res.getInt("rel_id"));
					json.put("metricValue", res.getInt("metric_value"));
					json.put("explanation", res.getString("explanation"));
					json.put("action", res.getString("action"));
					json.put("responseCount", res.getString("response_count"));
					result.put(json);
				}
			} catch (JSONException e) {
				Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving sentiment score", e);
			}
		} catch (SQLException e1) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving sentiment score", e1);
		}
		return result.toString();
	}

	public String getSelfPerception(int companyId, int functionId, int positionId, int locationId) {

		JSONArray result = new JSONArray();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getSelfPerception(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);

			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					JSONObject json = new JSONObject();
					json.put("relId", res.getInt("rel_id"));
					json.put("explanation", res.getString("explanation"));
					json.put("action", res.getString("action"));
					json.put("responseCount", res.getString("response_count"));
					
					
					JSONObject team= new JSONObject();
					team.put("stronglyDisagree", res.getInt("strongly_disagree"));
					team.put("disagree", res.getInt("disagree"));
					team.put("neutral", res.getInt("neutral"));
					team.put("agree", res.getInt("agree"));
					team.put("stronglyAgree", res.getInt("agree"));
					
					JSONObject org = new JSONObject();					
					org.put("stronglyDisagree", res.getInt("strongly_disagree_o"));
					org.put("disagree", res.getInt("disagree_o"));
					org.put("neutral", res.getInt("neutral_o"));
					org.put("agree", res.getInt("agree_o"));
					org.put("stronglyAgree", res.getInt("strongly_agree_o"));
					
					JSONObject level = new JSONObject();
					level.put("team", team);
					level.put("org", org);
					json.put("level", level);
					
					result.put(json);
				}
			}
		} catch (JSONException | SQLException e) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving self perception", e);
		}
		return result.toString();

	}
	
	
	public String getExploreData(int companyId) {

		JSONArray result = new JSONArray();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getExploreData()}");
			ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					JSONObject json = new JSONObject();
					json.put("indexValue", res.getInt("metric_value"));
					json.put("function", res.getString("Function"));
					json.put("position", res.getString("Position"));
					json.put("location", res.getString("Location"));
					json.put("relName", res.getString("rel_name"));
					result.put(json);
				}
			
		} catch (JSONException | SQLException e) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving self perception", e);
		}
		return result.toString();

	}
}
