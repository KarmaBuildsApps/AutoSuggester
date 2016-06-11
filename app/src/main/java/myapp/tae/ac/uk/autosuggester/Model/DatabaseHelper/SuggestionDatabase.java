package myapp.tae.ac.uk.autosuggester.Model.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Karma on 09/06/16.
 */
public class SuggestionDatabase extends SQLiteOpenHelper {
    private static final String dbName = "suggestionDatabase";
    private static final int version = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String KEY_PRIMARY = " INTEGER PRIMARY KEY AUTOINCREMENT"; // FIXME: 09/06/16
    private static final String NOT_NULL = " NOT NULL";
    private static final String BRACE_OPEN = "(";
    private static final String BACE_CLOSED = ")";

    private static final String CREATE_TABLE = "CREATE TABLE " + DatabaseContract.SuggestionEntries.TABLE_NAME +
            BRACE_OPEN + DatabaseContract.SuggestionEntries.COLUMN_ID + KEY_PRIMARY + COMMA_SEP +
            DatabaseContract.SuggestionEntries.COLUMN_WORDS + TEXT_TYPE + NOT_NULL + BACE_CLOSED;
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + DatabaseContract.SuggestionEntries.TABLE_NAME;
    private static final String CLEAR_TABLE = "DELETE * FROM " + DatabaseContract.SuggestionEntries.TABLE_NAME;

    public SuggestionDatabase(Context context) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void insertSuggestion(String suggestion) {
        ContentValues c = new ContentValues();
        c.put(DatabaseContract.SuggestionEntries.COLUMN_WORDS, suggestion);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(DatabaseContract.SuggestionEntries.TABLE_NAME, null, c);
        db.close();
    }

    public ArrayList<String> getSuggestions(String searchWord) {
        ArrayList<String> suggestions = new ArrayList<>();
        String[] columns = {DatabaseContract.SuggestionEntries.COLUMN_WORDS};
        String selection = DatabaseContract.SuggestionEntries.COLUMN_WORDS + "LIKE ? ";
        String arg[] = {searchWord};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (searchWord == null) {
            cursor = db.query(DatabaseContract.SuggestionEntries.TABLE_NAME, columns, null, null, null, null, null);
        } else {
            cursor =
                    db.query(DatabaseContract.SuggestionEntries.TABLE_NAME, columns, selection, arg, null, null, null);
        }
        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getColumnName(cursor.getColumnIndex(DatabaseContract.SuggestionEntries.COLUMN_WORDS));
                suggestions.add(word);
            } while (cursor.moveToNext());
        }
        db.close();
        return suggestions;
    }


    public void clearSuggestTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DatabaseContract.SuggestionEntries.TABLE_NAME, null, null);
    }
}
