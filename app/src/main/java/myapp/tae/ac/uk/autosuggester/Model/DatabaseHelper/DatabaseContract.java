package myapp.tae.ac.uk.autosuggester.Model.DatabaseHelper;

import android.provider.BaseColumns;

/**
 * Created by Karma on 09/06/16.
 */
public final class DatabaseContract {
    DatabaseContract() {
    }

    public static abstract class SuggestionEntries implements BaseColumns {
        public static final String TABLE_NAME = "autoSuggestion";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_WORDS = "WORDS";
    }
}
