package pl.bartekpawlowski.googlebookssearch;

/**
 * Public object for handling single Book
 */

public class GoogleBook {

    String mTitle;
    String mAuthor;

    public GoogleBook(String title, String author) {
        mTitle = title;
        mAuthor = author;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }
}
