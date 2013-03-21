package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

public class Parser { 
	private static final String[] SMALL_WORDS = {"az", "a", "és"};

	private static final String[] LARGE_WORDS = {"I.", "II.", "III.", "IV.", "V.", "VI.", "VII.", "VIII.", "IX.", "X."};

	public static void main(String[] args) {
		File sourceFolder= new File("e:\\work\\bible_app\\content");
		File outputFolder = new File("e:\\work\\bible_app\\content\\raw");
		File xmlFolder = new File("e:\\work\\bible_app\\content\\xml");
		//write(sourceFolder, outputFolder, xmlFolder, "03");
		parseAll(sourceFolder, outputFolder, xmlFolder);
	}
	
	public static void parseAll(File sourceFolder, File outputFolder, File xmlFolder) {
		for (int i = 1; i <= 73; i++){
			String filePrefix = (i < 10?"0":"") + i;
			try {
				write(sourceFolder, outputFolder, xmlFolder, filePrefix);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Parsing finished");		
	}
	
	public static void write(File sourceFolder, File outputFolder,  File xmlFolder, String filePrefix){
		System.out.println("Parsing file " + filePrefix);
		CleanerProperties props = new CleanerProperties();
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		props.setOmitDeprecatedTags(true);
		props.setOmitUnknownTags(true);
		props.setPruneTags("br,hr,BR,HR");
		HtmlCleaner cleaner = new HtmlCleaner(props);
		TagNode tagNode;
		try {
			tagNode = cleaner.clean(new File(sourceFolder, filePrefix + ".html"), "ISO8859-2");
			System.out.println("Root found " + tagNode);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(new File(outputFolder, filePrefix + ".txt")));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			@SuppressWarnings("unchecked")
			List<TagNode> tagNodes = tagNode.getAllChildren();
			for (Object node : tagNodes.get(1).getAllChildren()) {
				if (node instanceof TagNode) {
					if (((TagNode) node).getName().equals("h1")) {
						for (Object content : ((TagNode) node).getAllChildren()) {
							if (content instanceof ContentNode) {
								if (!content.toString().trim().equals("")) { 
									try {
										writer.write(capitalizeString(content.toString().trim()));
										writer.flush();
										break;
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
					} else {
						@SuppressWarnings("rawtypes")
						List pChildren = ((TagNode) node).getAllChildren();
						StringBuilder contentBuilder = new StringBuilder();
						for (Object pChild : pChildren){
							if (pChild instanceof TagNode && ((TagNode)pChild).getName().equals("b")) {
								for (Object bChild : ((TagNode)pChild).getAllChildren()) {
									if (bChild instanceof TagNode) {
										System.out.println(((TagNode)bChild).getAttributeByName("name"));
										writer.write("\nfa4dh6fed ");
										writer.write(((TagNode)bChild).getAttributeByName("name"));
										writer.write("\n");
										writer.flush();
									}
								}
							}
							if (pChild instanceof TagNode && ((TagNode)pChild).getName().equals("em")) {
								for (Object emChild : ((TagNode)pChild).getAllChildren()) {
									if (emChild instanceof ContentNode) {
										if (((ContentNode)emChild).getContent().trim().equals("[")) {
											continue;
										}
										if (((ContentNode)emChild).getContent().trim().equals("]")) {
											continue;
										}
										if (contentBuilder.length() > 0) {
											contentBuilder.append(" ");
										}
										contentBuilder.append(((ContentNode)emChild).getContent().trim());
									}									
								}
							}
							if (pChild instanceof ContentNode) {
								if (contentBuilder.length() > 0) {
									contentBuilder.append(" ");
								}
								contentBuilder.append(((ContentNode)pChild).getContent().trim());
							}
						}
						System.out.println(contentBuilder.toString());
						writer.write(contentBuilder.toString());
						writer.flush();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		props = new CleanerProperties();
		try {
			new PrettyXmlSerializer(props).writeToFile(tagNode, new File(xmlFolder, filePrefix + ".xml").getAbsolutePath(), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') {
				found = false;
			}
		}
		
		String capitalized = String.valueOf(chars);
		String[] parts = capitalized.split(" ");
		StringBuilder bulider = new StringBuilder();
		for (String part : parts) {
			for (String lowercase : SMALL_WORDS){
				if (part.toLowerCase().equals(lowercase)) {
					part = part.toLowerCase();
					break;
				}
			}
			for (String uppercase : LARGE_WORDS){
				if (part.toUpperCase().equals(uppercase)) {
					part = part.toUpperCase();
					break;
				}
			}
			if ((bulider.length() > 0) && (part.length() > 0)) {
				bulider.append(" ");
			}
			bulider.append(part);
		}
		return bulider.toString();
	}
}