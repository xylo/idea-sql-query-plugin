package com.kiwisoft.utils;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import com.intellij.util.net.HttpConfigurable;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 11.11.2006
 * Time: 22:59:20
 * To change this template use File | Settings | File Templates.
 */
public class WebUtils
{
	private static String proxyTestUrl="http://java.sstiller.de";

	private WebUtils()
	{
	}

	public static void setProxyTestUrl(String proxyTestUrl)
	{
		WebUtils.proxyTestUrl=proxyTestUrl;
	}

	/**
	 * Sends a POST request to an URL and passes the supplied parameters.
	 *
	 * @param parameters The key-value pairs to send in the POST request.
	 * @throws java.io.IOException If an I/O error occurs.
	 */
	public static String postRequest(String urlString, Map parameters) throws IOException
	{
		URL url=new URL(urlString);

		HttpConfigurable httpConfigurable=HttpConfigurable.getInstance();
		httpConfigurable.prepareURL(proxyTestUrl);
		HttpURLConnection connection=(HttpURLConnection)url.openConnection();

		// Create x-www-url-encoded parameter string
		String postData=buildQueryString(parameters);

		// Set up request
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);

		// Write the form data to the connection
		Writer writer=new OutputStreamWriter(connection.getOutputStream());
		writer.write(postData);
		writer.flush();
		writer.close();

		// Read the return from the connection
		char[] buffer=new char[1000];
		int length;
		Reader reader=new InputStreamReader(connection.getInputStream());
		StringBuffer output=new StringBuffer();
		while ((length=reader.read(buffer))!=-1) output.append(new String(buffer, 0, length));
		reader.close();

		connection.disconnect();

		return output.toString();
	}

	/**
	 * Builds a query string from the provided key-value-pairs. All
	 * spaces are substituted by '+' characters, and all non US-ASCII
	 * characters are escaped to hexadecimal notation (%xx).
	 */
	private static String buildQueryString(Map parameters) throws UnsupportedEncodingException
	{
		StringBuffer queryString=new StringBuffer(20*parameters.size());
		for (Iterator it=parameters.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry parameter=(Map.Entry)it.next();
			// Escape both key and value and combine them with an '='
			queryString.append(URLEncoder.encode((String)parameter.getKey(), "UTF-8"));
			queryString.append('=');
			queryString.append(URLEncoder.encode(String.valueOf(parameter.getValue()), "UTF-8"));
			if (it.hasNext()) queryString.append('&');
		}
		return queryString.toString();
	}
}
