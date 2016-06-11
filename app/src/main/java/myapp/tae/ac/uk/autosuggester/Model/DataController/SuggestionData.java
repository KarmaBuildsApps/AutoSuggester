package myapp.tae.ac.uk.autosuggester.Model.DataController;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.LoaderManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

import myapp.tae.ac.uk.autosuggester.Constants.Constants;
import myapp.tae.ac.uk.autosuggester.Model.DatabaseHelper.SuggestionDatabase;


/**
 * Created by Karma on 09/06/16.
 */
public class SuggestionData extends Observable implements AutoSuggestionDataContract {
    private static final String DEFAULT_SEPARATOR = " ";

    private SuggestionDatabase dbHelper;
    private Context context;
    private String separator = DEFAULT_SEPARATOR;
    private LoaderManager.LoaderCallbacks<Void> loaderCallbacks;

    public SuggestionData(Context context) {
        dbHelper = new SuggestionDatabase(context);
        this.context = context;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public void appendSuggestionFromFile(String filename) {
        new DataLoader((Activity) context).execute(filename);
    }

    @Override
    public void updateDB(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            dbHelper.clearSuggestTable();
            new DataLoader((Activity) context).execute(filename);
        }
    }

    @Override
    public ArrayList<String> getSuggestions(String suggestion) {
        return dbHelper.getSuggestions(suggestion);
    }

    @Override
    public void clearDatabase() {
        dbHelper.clearSuggestTable();
    }

    /**
     *
     */
    public class DataLoader extends AsyncTask<String, Void, Void> {
        private WeakReference<Activity> reference;

        public DataLoader(Activity context) {
            reference = new WeakReference<Activity>(context);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                String fileNam = params[0];
                if (reference.get() != null) {
                    File sdcard = Environment.getExternalStorageDirectory();
                    File file = new File(fileNam);
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    ArrayList<String> suggestionWordsFromFile = new ArrayList<>();
                    String read;
                    while ((read = br.readLine()) != null) {
                        read = read.replaceAll(separator + "{2,}", separator);
                        String[] suggestionWords = read.split(separator);
                        Collections.addAll(suggestionWordsFromFile, suggestionWords);
                    }
                    insertIntoDatabase(suggestionWordsFromFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                setChanged();
                notifyObservers(Constants.DATA_CHANGE_COMPLETE);
            }
            return null;
        }
    }

    /**
     * @param suggestionWords
     */
    private void insertIntoDatabase(ArrayList<String> suggestionWords) {
        String word;
        Set<String> refineSWords = new HashSet<>(suggestionWords);
        for (Iterator<String> itWords = refineSWords.iterator(); itWords.hasNext(); ) {
            word = itWords.next();
            dbHelper.insertSuggestion(word);
        }
    }
}
