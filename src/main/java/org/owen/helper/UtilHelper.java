package org.owen.helper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class UtilHelper {

	public static List<String> colorList = getColorList();
	public static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss"; // 2016-01-01 00:00:00
	public static final String dateFormat = "yyyy-MM-dd"; // 2016-01-01;

	public static int[] getIntArrayFromIntegerList(List<Integer> integerList) {
		int[] result = new int[integerList.size()];
		for (int i = 0; i <= integerList.size() - 1; i++) {
			result[i] = integerList.get(i);
		}
		return result;
	}

	public static java.sql.Date convertJavaDateToSqlDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static java.sql.Timestamp convertJavaDateToSqlTimestamp(java.util.Date date) {
		return new java.sql.Timestamp(date.getTime());
	}

	public static String getConfigProperty(String propertyName) {
		String propertyValue = "";

		Properties prop = new Properties();
		String propFileName = "config.properties";
		InputStream inputStream = UtilHelper.class.getClassLoader().getResourceAsStream(propFileName);
		if (inputStream != null) {
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				org.apache.log4j.Logger.getLogger(UtilHelper.class).error("property file '" + propFileName + "' not found in classpath");
			}
			propertyValue = prop.getProperty(propertyName);
		} else {
			org.apache.log4j.Logger.getLogger(UtilHelper.class).error("property file '" + propFileName + "' not found in classpath");
		}

		try {
			inputStream.close();
		} catch (IOException e) {
			org.apache.log4j.Logger.getLogger(UtilHelper.class).error("Error while closing the inputStream");
		}
		return propertyValue;
	}

	public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		for (int x = 1; x <= columns; x++) {
			if (columnName.equals(rsmd.getColumnName(x))) {
				return true;
			}
		}
		return false;
	}

	public static Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	public static int getIntValue(String val) {
		int iVal = 0;
		try {
			if (val != null) {
				iVal = Integer.parseInt(val);
			}
		} catch (NumberFormatException nfe) {
			org.apache.log4j.Logger.getLogger(UtilHelper.class).error("Error while getting integer value of " + val);
		}
		return iVal;
	}

	public static List<String> getColorList() {
		List<String> colorList = new ArrayList<>();

		colorList.add("#f44336");
		colorList.add("#E91E63");
		colorList.add("#9C27B0");
		colorList.add("#3F51B5");
		colorList.add("#2196F3");
		colorList.add("#009688");
		colorList.add("#4CAF50");
		colorList.add("#8BC34A");
		colorList.add("#CDDC39");
		colorList.add("#FFEB3B");
		colorList.add("#FFC107");
		colorList.add("#FF9800");
		colorList.add("#FF5722");
		colorList.add("#ef9a9a");
		colorList.add("#F48FB1");
		colorList.add("#CE93D8");
		colorList.add("#9FA8DA");
		colorList.add("#81D4FA");
		colorList.add("#80CBC4");
		colorList.add("#A5D6A7");
		colorList.add("#C5E1A5");
		colorList.add("#E6EE9C");
		colorList.add("#FFF59D");
		colorList.add("#FFCC80");
		colorList.add("#FFAB91");

		return colorList;
	}
}