package hu.droidium.bibliapp.database;

import android.provider.BaseColumns;

/**
 * Represents a tag. Tags can be added to verses. Each tag has a color that symbolizes highlighting.
 * @author Balazs Lajtha
 *
 */
public class TagMeta implements BaseColumns {
	public static final String TABLE_NAME = "tagMeta";
	public static final String COLUMN_NAME_TAG_ID = "tagId";
	public static final String COLUMN_NAME_TAG_NAME = "tagName";
	public static final String COLUMN_NAME_COLOR = "color";
	
	private String tagId;
	private String name;
	private String color;
	
	
	public TagMeta (String tagId, String name, String color) {
		this.tagId = tagId;
		this.name = name;
		this.color = color;
	}

	public String getId() {
		return tagId;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}
	public static String getCreateTableText() {
		String create = "CREATE TABLE ";
		create += TABLE_NAME + " (";
		create += COLUMN_NAME_TAG_ID + " TEXT PRIMARY KEY,";
		create += COLUMN_NAME_TAG_NAME + " TEXT,";
		create += COLUMN_NAME_COLOR + " TEXT)";
		return create;
	}

	public static String getDeleteTableText() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}

	public static TagMeta parse(String line) {
		String[] parts = line.split(";");
		if (parts.length == 3) {
			TagMeta tag = new TagMeta(parts[0], parts[1], parts[2]);
			return tag;
		}
		return null;
	}
}