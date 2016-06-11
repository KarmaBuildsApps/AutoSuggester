package myapp.tae.ac.uk.autosuggester;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import java.io.File;
import java.net.URISyntaxException;

import myapp.tae.ac.uk.autosuggester.Constants.Constants;
import myapp.tae.ac.uk.autosuggester.Extra.FileUtil;
import myapp.tae.ac.uk.autosuggester.Presenter.MainAutoSuggestionView;
import myapp.tae.ac.uk.autosuggester.Presenter.MainPresenter;
import myapp.tae.ac.uk.autosuggester.UI.CustomAutoComplete;
import myapp.tae.ac.uk.autosuggester.UI.adapters.AdapterAutoComplete;

public class MainActivity extends AppCompatActivity implements MainAutoSuggestionView {

    private CustomAutoComplete autoComplete;
    private AdapterAutoComplete adapter;
    private MainPresenter presenter;
    private ViewSwitcher viewSwitcher;
    private Button clear, update, insert;
    private ProgressBar progressBar;
    private boolean isProgressBarShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiateViews();
        presenter = new MainPresenter(MainActivity.this, this);
        adapter = new AdapterAutoComplete(this, presenter.getSuggestions());
        autoComplete.setAdapter(adapter);
    }

    private void initiateViews() {
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.buttonInsert:
                        selectAndAddFileData(Constants.INSERT_DATA);
                        break;
                    case R.id.buttonUpdate:
                        selectAndAddFileData(Constants.UPDATE_DATA);
                        break;
                    case R.id.buttonClear:
                        presenter.clearSuggestionDatabase();
                        break;
                    case R.id.autoSearch:
                        autoComplete.setFocusable(true);
                        autoComplete.setFocusableInTouchMode(true);
                        autoComplete.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(autoComplete, InputMethodManager.SHOW_IMPLICIT);
                        break;
                    default:
                }
            }
        };

        autoComplete = (CustomAutoComplete) findViewById(R.id.autoSearch);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        insert = (Button) findViewById(R.id.buttonInsert);
        update = (Button) findViewById(R.id.buttonUpdate);
        clear = (Button) findViewById(R.id.buttonClear);
        insert.setOnClickListener(buttonClickListener);
        update.setOnClickListener(buttonClickListener);
        clear.setOnClickListener(buttonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void selectAndAddFileData(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select Suggestion Data Source"),
                    requestCode);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "File Manger not Found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showButtons() {
        if(isProgressBarShown)
        viewSwitcher.showPrevious();
    }

    @Override
    public void showProgressBar() {
        if(!isProgressBarShown){
            viewSwitcher.showNext();
            isProgressBarShown = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Activity.RESULT_OK == resultCode){
            String filePath = null;
            try {
                filePath = FileUtil.getPath(this, data.getData());
                FileUtil.verifyStoragePermissions(this);
                switch (requestCode){
                    case Constants.INSERT_DATA:
                        presenter.insertSuggestionToDatabase(filePath);
                        break;
                    case Constants.UPDATE_DATA:
                        presenter.updateSuggestionDatabase(filePath);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
