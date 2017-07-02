package pl.bartekpawlowski.googlebookssearch;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GoogleBooksSearchActivity extends AppCompatActivity implements LoaderCallbacks<List<GoogleBook>>{

    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView mListTextPlaceholder;
    private GoogleBookAdapter mGoogleBookAdapter;
    private EditText mUserInput;
    private Button mButton;

    private boolean isNetworkActive;
    private String mUserInputString;

    private final static int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_books_search);

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isNetworkActive = networkInfo != null && networkInfo.isConnectedOrConnecting();

        mProgressBar = (ProgressBar) findViewById(R.id.listProgressBar);
        mProgressBar.setVisibility(View.GONE);

        mListTextPlaceholder = (TextView) findViewById(R.id.listItemTextPlaceholder);

        mListView = (ListView) findViewById(R.id.list);
        mListView.setEmptyView(mListTextPlaceholder);

        mUserInput = (EditText) findViewById(R.id.userInput);

        mButton = (Button) findViewById(R.id.userButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                if(getLoaderManager().getLoader(LOADER_ID) != null) {
                    getLoaderManager().destroyLoader(LOADER_ID);
                }
                mUserInputString = mUserInput.getText().toString();
                getLoaderManager().initLoader(LOADER_ID, null, GoogleBooksSearchActivity.this);
            }
        });

        mGoogleBookAdapter = new GoogleBookAdapter(this, new ArrayList<GoogleBook>());
        mListView.setAdapter(mGoogleBookAdapter);
    }

    @Override
    public Loader<List<GoogleBook>> onCreateLoader(int id, Bundle args) {
        return new GoogleBookLoader(this, mUserInputString);
    }

    @Override
    public void onLoadFinished(Loader<List<GoogleBook>> loader, List<GoogleBook> data) {
        mProgressBar.setVisibility(View.GONE);
        if(loader == null) {
            mListTextPlaceholder.setText(R.string.nothing_to_display);
        }

        if(!isNetworkActive) {
            mListTextPlaceholder.setText(R.string.no_network_connection);
        }

        mGoogleBookAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<GoogleBook>> loader) {
        mGoogleBookAdapter.clear();
    }
}
