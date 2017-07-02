package pl.bartekpawlowski.googlebookssearch;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 *
 */

public class GoogleBookLoader extends AsyncTaskLoader<List<GoogleBook>> {
    String mUrl;
    Context mContext;

    public GoogleBookLoader(Context context, String url) {
        super(context);
        mUrl = url;
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<GoogleBook> loadInBackground() {
        if(mUrl == null) {
            return null;
        }
        return QueryHelper.extractGoogleBookString(mUrl, mContext);
    }
}
