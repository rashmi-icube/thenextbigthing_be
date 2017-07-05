package org.owen.hr;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owen.helper.DatabaseConnectionHelper;

public class HrDashboardHelper {

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
				}
				Logger.getLogger(HrDashboardHelper.class).debug("Node list size : " + nodeList.length());
			}
		} catch (JSONException | SQLException e) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving team networks diagram nodes ", e);
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
					e.put("source", res.getInt("from_id"));
					e.put("target", res.getInt("to_id"));
					e.put("relId", res.getInt("rel_id"));
					e.put("weight", res.getDouble("weight"));
					edgeList.put(e);

				}
			}

			Logger.getLogger(HrDashboardHelper.class).debug("Edge list size : " + edgeList.length());
		} catch (SQLException | JSONException e) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving team networks diagram edges ", e);
		}
		return edgeList.toString();
	}

	public String getRelIndexValue(int companyId, int functionId, int positionId, int locationId) {
		JSONObject result = new JSONObject();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getRelIndexValue(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);
			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					JSONObject innerJson = new JSONObject();
					innerJson.put("indexValue", res.getDouble("metric_value"));
					innerJson.put("explanation", res.getString("explanation"));
					innerJson.put("action", res.getString("action"));
					innerJson.put("responseCount", res.getInt("response_count"));
					result.put(String.valueOf(res.getInt("rel_id")), innerJson);
				}
			}
		} catch (JSONException | SQLException e1) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving index value", e1);
		}
		return result.toString();
	}

	public String getRelKeyPeople(int companyId, int functionId, int positionId, int locationId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		JSONObject result = new JSONObject();
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
				}
				result.put("1", rel1);
				result.put("2", rel2);
				result.put("3", rel3);
				result.put("4", rel4);
			}
		} catch (JSONException | SQLException e) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving key people", e);
		}
		System.out.println(result);
		return result.toString();
	}

	public String getWordCloud(int companyId, int functionId, int positionId, int locationId) {
		JSONObject result = new JSONObject();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getWordCloud(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);

			Map<Integer, List<Map<String, Object>>> resultMap = new HashMap<>();
			String[] wordList = { "positive", "neutral", "negative" };
			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					int relId = res.getInt("rel_id");

					Map<String, Object> innerMap = new HashMap<>();
					innerMap.put("word", res.getString("word"));
					innerMap.put("frequency", res.getInt("weight"));

					// TODO : Get associated words from db
					innerMap.put("association", generateRandomWords(5));

					// TODO : Replace with actual sentiment from db
					String randomString = wordList[(int) (Math.random() * wordList.length)];
					innerMap.put("sentiment", randomString);

					if (resultMap.containsKey(relId)) {
						resultMap.get(relId).add(innerMap);
					} else {
						resultMap.put(relId, new ArrayList<>());
						resultMap.get(relId).add(innerMap);
					}
				}
			}

			for (int relationshipId : resultMap.keySet()) {
				List<Map<String, Object>> list = resultMap.get(relationshipId);
				JSONArray innerArray = new JSONArray();
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> innerMap = list.get(i);
					JSONObject innerObject = new JSONObject();
					for (String key : innerMap.keySet()) {
						innerObject.put(key, innerMap.get(key));
					}
					innerArray.put(innerObject);
				}
				result.put(String.valueOf(relationshipId), innerArray);
			}
		} catch (JSONException | SQLException e) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving word cloud", e);
		}
		return result.toString();
	}

	public static StringBuffer generateRandomWords(int numberOfWords) {
		StringBuffer randomStrings = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < numberOfWords; i++) {
			char[] word = new char[random.nextInt(8) + 3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
			for (int j = 0; j < word.length; j++) {
				word[j] = (char) ('a' + random.nextInt(26));
			}
			randomStrings.append(", ");
			randomStrings.append(word);
		}
		return randomStrings;
	}

	public String getSentimentScore(int companyId, int functionId, int positionId, int locationId) {
		JSONObject result = new JSONObject();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getSentimentScore(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);

			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					JSONObject innerObj = new JSONObject();
					innerObj.put("relId", res.getInt("rel_id"));
					innerObj.put("metricValue", res.getInt("metric_value"));
					innerObj.put("explanation", res.getString("explanation"));
					innerObj.put("action", res.getString("action"));
					innerObj.put("responseCount", res.getInt("response_count"));
					result.put(String.valueOf(res.getInt("rel_id")), innerObj);
				}
			}
		} catch (JSONException | SQLException e1) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving sentiment score", e1);
		}
		return result.toString();
	}

	public String getSelfPerception(int companyId, int functionId, int positionId, int locationId) {

		JSONObject result = new JSONObject();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getSelfPerception(?,?,?)}")) {
			cstmt.setInt("fun", functionId);
			cstmt.setInt("pos", positionId);
			cstmt.setInt("loc", locationId);

			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					int relId = res.getInt("rel_id");
					JSONObject innerObj = new JSONObject();

					float teamTotalResponse = res.getInt("strongly_disagree") + res.getInt("disagree") + res.getInt("neutral") + res.getInt("agree")
							+ res.getInt("strongly_agree");

					float orgTotalResponse = res.getInt("strongly_disagree_o") + res.getInt("disagree_o") + res.getInt("neutral_o")
							+ res.getInt("agree_o") + res.getInt("strongly_agree_o");

					JSONArray jArray = new JSONArray();

					JSONObject sd = new JSONObject();
					sd.put("name", "Strongly Disagree");
					Integer[] dataArraySD = new Integer[2];
					dataArraySD[0] = Math.round((((float) res.getInt("strongly_disagree_o") / orgTotalResponse) * 100));
					dataArraySD[1] = Math.round((((float) res.getInt("strongly_disagree") / teamTotalResponse) * 100));
					sd.put("data", dataArraySD);
					jArray.put(sd);

					JSONObject d = new JSONObject();
					d.put("name", "Disagree");
					Integer[] dataArrayD = new Integer[2];
					dataArrayD[0] = Math.round((((float) res.getInt("disagree_o") / orgTotalResponse) * 100));
					dataArrayD[1] = Math.round((((float) res.getInt("disagree") / teamTotalResponse) * 100));
					d.put("data", dataArrayD);
					jArray.put(d);

					JSONObject n = new JSONObject();
					n.put("name", "Neutral");
					Integer[] dataArrayN = new Integer[2];
					dataArrayN[0] = Math.round((((float) res.getInt("neutral_o") / orgTotalResponse) * 100));
					dataArrayN[1] = Math.round((((float) res.getInt("neutral") / teamTotalResponse) * 100));
					n.put("data", dataArrayN);
					jArray.put(n);

					JSONObject a = new JSONObject();
					a.put("name", "Agree");
					Integer[] dataArray = new Integer[2];
					dataArray[0] = Math.round((((float) res.getInt("agree_o") / orgTotalResponse) * 100));
					dataArray[1] = Math.round((((float) res.getInt("agree") / teamTotalResponse) * 100));
					a.put("data", dataArray);
					jArray.put(a);

					JSONObject sa = new JSONObject();
					sa.put("name", "Strongly Agree");
					Integer[] dataArraySA = new Integer[2];
					dataArraySA[0] = Math.round((((float) res.getInt("strongly_agree_o") / orgTotalResponse) * 100));
					dataArraySA[1] = Math.round((((float) res.getInt("strongly_agree") / teamTotalResponse) * 100));
					sa.put("data", dataArraySA);
					jArray.put(sa);

					innerObj.put("relName", res.getString("rel_name"));
					innerObj.put("explanation", res.getString("explanation"));
					innerObj.put("action", res.getString("action"));
					innerObj.put("responseCount", res.getInt("response_count"));
					innerObj.put("series", jArray);
					result.put(String.valueOf(relId), innerObj);
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
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall("{call getExploreData()}");
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
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving explore data", e);
		}
		return result.toString();

	}

	public String getSentimentDistribution(int companyId, int functionId, int positionId, int locationId) {

		Logger.getLogger(HrDashboardHelper.class).debug("Entering getSentimentDistribution");
		JSONObject result = new JSONObject();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		Map<Integer, Map<String, Integer>> resultMap = new HashMap<>();

		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getSentimentDistribution()}")) {
			// cstmt.setInt("fun", functionId);
			// cstmt.setInt("pos", positionId);
			// cstmt.setInt("loc", locationId);

			try (ResultSet res = cstmt.executeQuery()) {
				while (res.next()) {
					int relId = res.getInt("rel_id");
					if (resultMap.containsKey(relId)) {
						Map<String, Integer> innerMap = resultMap.get(relId);
						innerMap.put(res.getString("sentiment"), res.getInt("sent_count"));
						resultMap.put(relId, innerMap);
					} else {
						Map<String, Integer> innerMap = new HashMap<>();
						innerMap.put(res.getString("sentiment"), res.getInt("sent_count"));
						resultMap.put(relId, innerMap);
					}
				}
			}

			for (int relationshipId : resultMap.keySet()) {

				Map<String, Integer> sourceMap = resultMap.get(relationshipId);
				JSONArray innerArray = new JSONArray();
				for (String key : sourceMap.keySet()) {
					JSONObject innerObj = new JSONObject();
					innerObj.put("name", key);
					String color = "";
					if (key.equalsIgnoreCase("positive")) {
						color = "#00C853";
					} else if (key.equalsIgnoreCase("negative")) {
						color = "#DD2C00";
					} else {
						color = "#FFD600";
					}

					String[] dataArray = new String[2];
					dataArray[0] = "y : " + sourceMap.get(key);
					dataArray[1] = "color : " + color;

					JSONObject dataObj = new JSONObject();
					dataObj.put("y", sourceMap.get(key));
					dataObj.put("color", color);
					JSONArray dataArr = new JSONArray();
					dataArr.put(dataObj);
					innerObj.put("data", dataArr);

					innerArray.put(innerObj);
				}
				result.put(String.valueOf(relationshipId), innerArray);
			}

		} catch (Exception e) {
			Logger.getLogger(HrDashboardHelper.class).error("Error while retrieving sentiment distribution", e);
		}
		dch.releaseRcon();
		return result.toString();

	}

}
