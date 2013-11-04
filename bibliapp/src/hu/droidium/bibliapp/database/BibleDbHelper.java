package hu.droidium.bibliapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BibleDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "BibleDatabase";
	private static final String SQL_CREATE_ENTRIES; 
	static {
		String create = "";
		create += Bookmark.getCreateTableText();
		create += Tag.getCreateTableText();
		create += TagMeta.getCreateTableText();
		SQL_CREATE_ENTRIES = create;
	}


	public BibleDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String update = ""; 
		if (oldVersion < 2) {
			// Add Tag and TagMeta tables
			update += Tag.getCreateTableText();
			update += TagMeta.getCreateTableText();
		}
		if (update.length() > 0) {
			db.execSQL(update);
		}
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}