package com.grepx.notedata;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.grepx.notedata.NotesPersistenceContract.NoteEntry.TABLE_NAME;

public class NotesPersistenceContract {

  private static final String CONTENT_SCHEME = "content://";

  public final String contentAuthority;

  public final String contentNoteListType;

  public final String contentNoteItemType;

  public final Uri baseContentUri;

  public final Uri contentNoteUri;

  public NotesPersistenceContract(String contentAuthority) {
    this.contentAuthority = contentAuthority;
    contentNoteListType = "vnd.android.cursor.dir/" + contentAuthority + "/" + TABLE_NAME;
    contentNoteItemType = "vnd.android.cursor.item/" + contentAuthority + "/" + TABLE_NAME;
    baseContentUri = Uri.parse(CONTENT_SCHEME + contentAuthority);
    contentNoteUri = baseContentUri.buildUpon().appendPath(TABLE_NAME).build();
  }

  public Uri buildTasksUriWith(long id) {
    return ContentUris.withAppendedId(contentNoteUri, id);
  }

  public Uri buildNotesUriWith(String id) {
    Uri uri = contentNoteUri.buildUpon().appendPath(id).build();
    return uri;
  }

  public Uri buildNotesUri() {
    return contentNoteUri.buildUpon().build();
  }

  public static abstract class NoteEntry implements BaseColumns {

    public static final String TABLE_NAME = "note";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_BODY = "body";

    public static final String[] NOTES_COLUMNS = new String[] {
        _ID,
        COLUMN_NAME_TITLE,
        COLUMN_NAME_BODY,
        };
  }
}
