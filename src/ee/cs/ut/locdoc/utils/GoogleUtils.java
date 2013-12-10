package ee.cs.ut.locdoc.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

public class GoogleUtils extends AsyncTask<Double, Void, String> {
	@Override
	protected String doInBackground(Double... params) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			
			String url = "http://maps.googleapis.com/maps/api/geocode/xml?latlng=" + params[0] + ","
					+ params[1] + "&sensor=false&language=EN";
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");

			StringWriter writer = new StringWriter();
			IOUtils.copy(con.getInputStream(), writer);
			String theString = writer.toString();
			
			Document dDoc = db.parse(new InputSource(new ByteArrayInputStream(theString.getBytes("utf-8"))));

			XPath xPath = XPathFactory.newInstance().newXPath();
			String locationName = xPath.compile("//result[last()-1]/address_component/short_name").evaluate(dDoc);
			return locationName;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return "";
	}
}
