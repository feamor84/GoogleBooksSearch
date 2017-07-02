package pl.bartekpawlowski.googlebookssearch;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GoogleBooksSearchActivity extends AppCompatActivity implements LoaderCallbacks<List<GoogleBook>>{

    private final static int LOADER_ID = 0;
    private final static String LIST_VIEW_STATE = "list_view_state";
    private final static String LOG_TAG = "Main activity";
    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView mListTextPlaceholder;
    private GoogleBookAdapter mGoogleBookAdapter;
    private EditText mUserInput;
    private Button mButton;
    private boolean isNetworkActive;
    private String mUserInputString;
    private Parcelable mListViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_books_search);

        mUserInputString = "";

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isNetworkActive = networkInfo != null && networkInfo.isConnectedOrConnecting();

        mProgressBar = (ProgressBar) findViewById(R.id.listProgressBar);
        mProgressBar.setVisibility(View.GONE);

        mListTextPlaceholder = (TextView) findViewById(R.id.listItemTextPlaceholder);

        mListView = (ListView) findViewById(R.id.list);
        mListView.setEmptyView(mListTextPlaceholder);
        mListTextPlaceholder.setText(R.string.insert_text_to_input);

        mUserInput = (EditText) findViewById(R.id.userInput);

        mButton = (Button) findViewById(R.id.userButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUserInput.getText().length() != 0) {
                    mUserInputString = mUserInput.getText().toString();
                    mProgressBar.setVisibility(View.VISIBLE);
                    getLoaderManager().restartLoader(LOADER_ID, null, GoogleBooksSearchActivity.this);
                } else {
                    Toast.makeText(GoogleBooksSearchActivity.this, getResources().getString(R.string.empty_input), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mGoogleBookAdapter = new GoogleBookAdapter(this, new ArrayList<GoogleBook>());
        mListView.setAdapter(mGoogleBookAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListView = (ListView) findViewById(R.id.list);
        mListViewState = mListView.onSaveInstanceState();
        outState.putParcelable(LIST_VIEW_STATE, mListViewState);
        Log.i(LOG_TAG, "onSaveInstanceState with " + mListViewState.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mListViewState = savedInstanceState.getParcelable(LIST_VIEW_STATE);
        Log.i(LOG_TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onResume() {
        if (mListViewState != null) {
            Log.i(LOG_TAG, "App reasume with mListViewState" + mListViewState.toString());
            mListView.onRestoreInstanceState(mListViewState);
        }
        super.onResume();
    }

    @Override
    public Loader<List<GoogleBook>> onCreateLoader(int id, Bundle args) {
        return new GoogleBookLoader(this, mUserInputString);
    }

    @Override
    public void onLoadFinished(Loader<List<GoogleBook>> loader, List<GoogleBook> data) {
        mProgressBar.setVisibility(View.GONE);

        mGoogleBookAdapter.clear();

        if(!isNetworkActive) {
            mListTextPlaceholder.setText(R.string.no_network_connection);
        }

        if (data.isEmpty() && !mUserInputString.isEmpty()) {
            mListTextPlaceholder.setText(R.string.nothing_to_display);
        }

        mGoogleBookAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<GoogleBook>> loader) {
        mGoogleBookAdapter.clear();
    }
}
