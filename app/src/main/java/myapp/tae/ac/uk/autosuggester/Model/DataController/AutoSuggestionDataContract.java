package myapp.tae.ac.uk.autosuggester.Model.DataController;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Karma on 09/06/16.
 */
public interface AutoSuggestionDataContract {
    public void appendSuggestionFromFile(String filename);
    public void updateDB(String filename);
    public ArrayList<String> getSuggestions(String suggestion);
    public void clearDatabase();

}
