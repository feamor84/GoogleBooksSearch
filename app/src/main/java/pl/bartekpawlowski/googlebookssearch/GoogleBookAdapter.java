package pl.bartekpawlowski.googlebookssearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for GoogleBook object
 */

public class GoogleBookAdapter extends ArrayAdapter<GoogleBook> {

    public GoogleBookAdapter(Context context, List<GoogleBook> googleBooks) {
        super(context, 0, googleBooks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View googleBooksView = convertView;
        if(googleBooksView == null) {
            googleBooksView = LayoutInflater.from(getContext()).inflate(R.layout.google_books_search_list_item, parent, false);
        }

        final GoogleBook currentGoogleBook = getItem(position);

        TextView googleBookTitle = (TextView) googleBooksView.findViewById(R.id.listItemTitle);
        googleBookTitle.setText(currentGoogleBook.getTitle());

        TextView googleBookAuthor = (TextView) googleBooksView.findViewById(R.id.listItemAuthor);
        googleBookAuthor.setText(currentGoogleBook.getAuthor());

        return googleBooksView;
    }
}
