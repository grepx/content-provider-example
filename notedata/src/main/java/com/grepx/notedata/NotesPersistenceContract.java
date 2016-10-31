package com.grepx.notedata;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class NotesPersistenceContract {
  public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;

  public static final String CONTENT_NOTE_LIST_TYPE =
      "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + NoteEntry.TABLE_NAME;

  public static final String CONTENT_NOTE_ITEM_TYPE =
      "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + NoteEntry.TABLE_NAME;

  private static final String CONTENT_SCHEME = "content://";

  public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEME + CONTENT_AUTHORITY);

  private NotesPersistenceContract() {
  }

  public static Uri getBaseNoteUri(String noteId) {
    return Uri.parse(CONTENT_SCHEME + CONTENT_NOTE_ITEM_TYPE + "/" + noteId);
  }

  public static abstract class NoteEntry implements BaseColumns {

    public static final String TABLE_NAME = "note";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_BODY = "body";

    public static final Uri CONTENT_NOTE_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
    public static String[] NOTES_COLUMNS = new String[] {
        _ID,
        COLUMN_NAME_TITLE,
        COLUMN_NAME_BODY,
        };

    public static Uri buildTasksUriWith(long id) {
      return ContentUris.withAppendedId(CONTENT_NOTE_URI, id);
    }

    public static Uri buildNotesUriWith(String id) {
      Uri uri = CONTENT_NOTE_URI.buildUpon().appendPath(id).build();
      return uri;
    }

    public static Uri buildNotesUri() {
      return CONTENT_NOTE_URI.buildUpon().build();
    }
  }
}
