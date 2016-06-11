package myapp.tae.ac.uk.autosuggester.Presenter;

import android.content.Context;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import myapp.tae.ac.uk.autosuggester.Constants.Constants;
import myapp.tae.ac.uk.autosuggester.Model.DataController.SuggestionData;

/**
 * Created by Karma on 10/06/16.
 */
public class MainPresenter implements Observer {
    private final Context context;
    private final MainAutoSuggestionView view;
    private SuggestionData dataController;

    public MainPresenter(Context context, MainAutoSuggestionView view) {
        this.context = context;
        this.view = view;
        dataController = new SuggestionData(context);
        dataController.addObserver(this);
    }

    public void insertSuggestionToDatabase(String fileName) {
        dataController.appendSuggestionFromFile(fileName);
        view.showProgressBar();
    }

    public void updateSuggestionDatabase(String fileName) {
        dataController.updateDB(fileName);
        view.showProgressBar();
    }

    public void clearSuggestionDatabase() {
        dataController.clearDatabase();
        view.updateAdapterData();
    }

    public ArrayList<String> getSuggestions() {
        return dataController.getSuggestions(null);
    }

    @Override
    public void update(Observable observable, Object data) {
        if((int) data==Constants.DATA_CHANGE_COMPLETE){
            view.showButtons();
            view.updateAdapterData();
        }
    }

    public void setFileDataSeparator(CharSequence fileDataSeparator) {
        dataController.setSeparator(fileDataSeparator.toString());
    }
}
