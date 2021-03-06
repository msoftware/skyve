package org.skyve.impl.bizport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.skyve.CORE;
import org.skyve.domain.messages.UploadException;
import org.skyve.domain.types.DateTime;
import org.skyve.domain.types.converters.Converter;
import org.skyve.util.Util;

public class DelimitedLoader extends AbstractDataFileLoader {

	private BufferedReader fileReader;
	private String seperator;
	private String[] header;
	protected Map<String, Object> valueMap;

	public void setValueMap(Map<String, Object> valueMap) {
		this.valueMap = valueMap;
	}

	public Map<String, Object> getValueMap() {
		return valueMap;
	}

	public DelimitedLoader(LoaderActivityType activityType, InputStream fileInputStream, UploadException exception,
			String moduleName, String documentName, String delimiter, String... bindings) throws Exception {

		super(activityType, exception, moduleName, documentName);
		addFields(bindings);

		this.seperator = "" + delimiter;
		fileReader = new BufferedReader(new InputStreamReader(fileInputStream));

		header = splitLine();

		setDataIndex(0);
	}

	@Override
	public void finalize() {
		try {
			if (fileReader != null) {
				fileReader.close();
			}
		} catch (@SuppressWarnings("unused") Exception e) {
			Util.LOGGER.log(Level.SEVERE, "Exception thrown closing input file");
		}
	}

	@Override
	public String getStringFieldValue(int index, boolean blankAsNull) throws Exception {
		String hdr = header[index];
		if (getValueMap() != null && getValueMap().containsKey(hdr)) {
			String value = (String) getValueMap().get(hdr);
			if (blankAsNull && null != value && "".equals(value.trim())) {
				return null;
			}

			return value;
		}

		return null;
	}

	@Override
	public Date getDateFieldValue(int index) throws Exception {
		String value = getStringFieldValue(index, true);
		if (null == value)
			return null;

		Converter<DateTime> converter = CORE.getPersistence().getUser().getCustomer().getDefaultDateTimeConverter();
		Date result = converter.fromDisplayValue(value);
		return result;
	}

	@Override
	public Double getNumericFieldValue(int index, boolean emptyAsZero) throws Exception {
		String value = getStringFieldValue(index, true);
		if (null == value)
			return new Double(0);

		return Double.valueOf(value);
	}

	@Override
	public void nextData() throws Exception {
		String[] fieldValues = splitLine();
		if (null == fieldValues) {
			setValueMap(null);
			return;
		}

		Map<String, Object> values = new HashMap<>();
		for (int cntr = 0; cntr < header.length; ++cntr) {
			String currentValue = fieldValues[cntr];
			// remove any surrounding double quotes from this CSV string, if any
			if (currentValue != null) {
				// trim the string
				currentValue = currentValue.trim();

				if (currentValue.indexOf("\"") >= 0) {
					// remove any surrounding double quotes from this CSV string, if any
					currentValue = removeQuotesSurroundingString(currentValue);

					// replace an escaped double quote with a single double quote ("")
					currentValue = replaceEscapedDoubleQuote(currentValue);

					// trim the string after the quotes were stripped
					currentValue = currentValue != null ? currentValue.trim() : null;
				}
			}

			values.put(header[cntr], currentValue);
		}
		setValueMap(values);
		dataIndex++;
	}

	@Override
	public boolean hasNextData() throws Exception {
		return true;
	}

	@Override
	public boolean isNoData() throws Exception {
		return (valueMap == null);
	}

	@Override
	public String getWhere(int index) throws Exception {
		StringBuilder where = new StringBuilder(128);
		where.append("Line ").append((getDataIndex()));
		where.append(" ").append(header[index]);
		where.append(".");
		return where.toString();
	}

	@Override
	public String debugData() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("Line ").append(getDataIndex());

		if (valueMap != null) {
			for (String key : getValueMap().keySet()) {
				sb.append(", (").append(getDataIndex()).append(",").append(key).append(") = ");
				sb.append(valueMap.get(key).toString());
			}
		} else {
			sb.append(" Null");
		}
		return sb.toString();
	}

	/**
	 * Removes surrounding quotes from the specified string.
	 * 
	 * @param string The string to cleanse
	 * @return The string without double quotes at the beginning and end, if it had any
	 */
	static String removeQuotesSurroundingString(String string) {
		if (string != null && string.startsWith("\"") && string.endsWith("\"")) {
			return string.length() > 1 ? string.substring(1, string.length() - 1) : "";
		}
		return string;
	}

	/**
	 * Replaces a CSV escaped double quote ("") with a single double
	 * quote when present in the input value.
	 * 
	 * @param string The string to cleanse
	 * @return The string without two consecutive double quotes, if it had any
	 */
	static String replaceEscapedDoubleQuote(String string) {
		if (string != null && string.contains("\"\"")) {
			return string.replaceAll("\"\"", "\"");
		}
		return string;
	}

	static String[] splitLine(String line, String delimiter) {
		if (null == line) {
			return null;
		}

		String[] parts = line.split(delimiter);

		List<String> values = new ArrayList<>();
		String part;
		for (int cntr = 0; cntr < parts.length; ++cntr) {
			part = parts[cntr];
			if (part.startsWith("\"")) {
				while (!part.endsWith("\"")) {
					part += delimiter + parts[++cntr];
				}
			}
			values.add(part);
		}

		if (line.endsWith(delimiter)) {
			values.add("");
		}

		return values.toArray(new String[values.size()]);
	}

	private String[] splitLine() throws Exception {
		String line = fileReader.readLine();

		return splitLine(line, seperator);
	}
}
