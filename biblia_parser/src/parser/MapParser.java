package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MapParser extends DefaultHandler {
	
	private boolean inPlacemark = false;
	private boolean inName = false;
	private boolean inCoords = false;
	private boolean inDescription = false;
	private PlaceMark actualPlacemark;
	private List<PlaceMark> placeMarks = new ArrayList<PlaceMark>();
	private StringBuffer content = new StringBuffer();

	public MapParser(InputStream input) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(input, this);
		} catch (ParserConfigurationException e) {
			System.out.println("ParserConfig error");
		} catch (SAXException e) {
			System.out.println("SAXException : xml not well formed");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO error");
		}
	}

	@Override
	public void startElement(String s, String s1, String elementName,
			Attributes attributes) throws SAXException {
		if (elementName.equalsIgnoreCase("placemark")) {
			if (inPlacemark) {
				throw new SAXException("Entered two times into placemarks");
			}
			inPlacemark = true;
			actualPlacemark = new PlaceMark();
		} else if (inPlacemark && elementName.equals("name")) {
			inName = true;
		} else if (inPlacemark && elementName.equals("coordinates")) {
			inCoords = true;
		} else if (inPlacemark && elementName.equals("description")) {
			inDescription = true;
		}
	}

	@Override
	public void endElement(String s, String s1, String elementName)
			throws SAXException {
		if (elementName.equalsIgnoreCase("placemark")) {
			inPlacemark = false;
			placeMarks.add(actualPlacemark);
		} else if (inPlacemark && elementName.equals("name")) {
			if (inName) {
				actualPlacemark.setName(content.toString());
			}
			inName = false;
		} else if (inPlacemark && elementName.equals("coordinates")) {
			if (inCoords) {
				actualPlacemark.setCoords(content.toString());
			}
			inCoords = false;
		} else if (inPlacemark && elementName.equals("description")) {
			if (inDescription) {
				actualPlacemark.setVerses(content.toString());
			}
			inDescription = false;
		}
		content.delete(0, content.length());
	}

	@Override
	public void characters(char[] chars, int offset, int len) throws SAXException {
		content.append(chars, offset, len);
	}
	
	public List<PlaceMark> getMarks() {
		return new ArrayList<PlaceMark>(placeMarks);
	}
	
	public static void export(List<PlaceMark> places, File outputFolder, String filePrefix) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFolder, filePrefix)),"UTF-8"));
			for (PlaceMark place : places) {
				try {
					writer.write(place.getOutput());
					writer.write("\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	private void log(String message) {
		System.out.println(message);
	}
}