package myapp.tae.ac.uk.autosuggester;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

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
    private ProgressBar progressBar, autoCompleteProgressBar;
    private boolean isProgressBarShown = false;
    private String tempFilePath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiateViews();
        presenter = new MainPresenter(MainActivity.this, this);
        autoComplete.setLoadingIndicator(autoCompleteProgressBar);
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
        autoCompleteProgressBar = (ProgressBar) findViewById(R.id.autocompleteProgressBar);
        insert = (Button) findViewById(R.id.buttonInsert);
        update = (Button) findViewById(R.id.buttonUpdate);
        clear = (Button) findViewById(R.id.buttonClear);
        insert.setOnClickListener(buttonClickListener);
        update.setOnClickListener(buttonClickListener);
        clear.setOnClickListener(buttonClickListener);
    }

    @Override
    public void updateAdapterData() {
        adapter.setAutoSuggestions(presenter.getSuggestions());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSetSeparator:
                showSeparatorDialogOption();
                break;
            case R.id.menuSetting:
                Toast.makeText(this, "Setting Clicked", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSeparatorDialogOption() {
        AlertDialog.Builder separatorDialog = new AlertDialog.Builder(this);
        separatorDialog.setTitle("Set Separator");
        separatorDialog.setMessage("Enter Separator Like | or , etc.");
        final EditText etSeparator = new EditText(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        etSeparator.setLayoutParams(layoutParams);
        separatorDialog.setView(etSeparator);
        separatorDialog.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CharSequence separator = etSeparator.getText();
                presenter.setFileDataSeparator(separator);
            }
        });
        separatorDialog.show();
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
        if(isProgressBarShown) {
            viewSwitcher.showPrevious();
            adapter.setAutoSuggestions(presenter.getSuggestions());
            isProgressBarShown = false;
        }
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
                if(Build.VERSION.SDK_INT>=23){
                    tempFilePath = filePath;
                    FileUtil.verifyStoragePermissions(this, requestCode);
                }else {
                    switch (requestCode) {
                        case Constants.INSERT_DATA:
                            presenter.insertSuggestionToDatabase(filePath);
                            break;
                        case Constants.UPDATE_DATA:
                            presenter.updateSuggestionDatabase(filePath);
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==Constants.INSERT_DATA){
            presenter.insertSuggestionToDatabase(tempFilePath);
        }else if(requestCode== Constants.UPDATE_DATA){
            presenter.updateSuggestionDatabase(tempFilePath);
        }
    }

    public class DatabaseLoader extends AsyncTaskLoader<ArrayList<String>>{

        public DatabaseLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<String> loadInBackground() {
            return presenter.getSuggestions();
        }
    }
}
