package hu.droidium.bibliapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BibleDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "BibleDatabase";

	public BibleDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Bookmark.getCreateTableText());
		db.execSQL(Tag.getCreateTableText());
		db.execSQL(TagMeta.getCreateTableText());
		db.execSQL(Translation.getCreateTableText());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			// Add Tag and TagMeta tables
			db.execSQL(Tag.getCreateTableText());
			db.execSQL(TagMeta.getCreateTableText());
			db.execSQL(Translation.getCreateTableText());
		}
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}