package com.grepx.notedata;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class NotesProvider extends ContentProvider {

  private static final int NOTE_LIST = 100;
  private static final int NOTE_ITEM = 101;
  private static final UriMatcher sUriMatcher = buildUriMatcher();
  private NotesDbHelper mNotesDbHelper;

  private static UriMatcher buildUriMatcher() {
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = NotesPersistenceContract.CONTENT_AUTHORITY;

    matcher.addURI(authority, NotesPersistenceContract.NoteEntry.TABLE_NAME, NOTE_LIST);
    matcher.addURI(authority, NotesPersistenceContract.NoteEntry.TABLE_NAME + "/*", NOTE_ITEM);

    return matcher;
  }

  @Override
  public boolean onCreate() {
    mNotesDbHelper = new NotesDbHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public String getType(Uri uri) {
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case NOTE_LIST:
        return NotesPersistenceContract.CONTENT_NOTE_LIST_TYPE;
      case NOTE_ITEM:
        return NotesPersistenceContract.CONTENT_NOTE_ITEM_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  @Nullable
  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                      String sortOrder) {
    Cursor retCursor;
    switch (sUriMatcher.match(uri)) {
      case NOTE_LIST:
        retCursor = mNotesDbHelper.getReadableDatabase().query(
            NotesPersistenceContract.NoteEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
                                                              );
        break;
      case NOTE_ITEM:
        String[] where = { uri.getLastPathSegment() };
        retCursor = mNotesDbHelper.getReadableDatabase().query(
            NotesPersistenceContract.NoteEntry.TABLE_NAME,
            projection,
            NotesPersistenceContract.NoteEntry._ID + " = ?",
            where,
            null,
            null,
            sortOrder
                                                              );
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
    return retCursor;
  }

  @Nullable
  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final SQLiteDatabase db = mNotesDbHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    Uri returnUri;

    switch (match) {
      case NOTE_LIST:
        Cursor exists = db.query(
            NotesPersistenceContract.NoteEntry.TABLE_NAME,
            new String[] { NotesPersistenceContract.NoteEntry._ID },
            NotesPersistenceContract.NoteEntry._ID + " = ?",
            new String[] {
                values.getAsString(NotesPersistenceContract.NoteEntry._ID)
            },
            null,
            null,
            null
                                );
        if (exists.moveToLast()) {
          long _id = db.update(
              NotesPersistenceContract.NoteEntry.TABLE_NAME, values,
              NotesPersistenceContract.NoteEntry._ID + " = ?",
              new String[] {
                  values.getAsString(NotesPersistenceContract.NoteEntry._ID)
              }
                              );
          if (_id > 0) {
            returnUri = NotesPersistenceContract.NoteEntry.buildTasksUriWith(_id);
          } else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
          }
        } else {
          long _id = db.insert(NotesPersistenceContract.NoteEntry.TABLE_NAME, null, values);
          if (_id > 0) {
            returnUri = NotesPersistenceContract.NoteEntry.buildTasksUriWith(_id);
          } else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
          }
        }
        exists.close();
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return returnUri;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = mNotesDbHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsDeleted;

    switch (match) {
      case NOTE_LIST:
        rowsDeleted = db.delete(
            NotesPersistenceContract.NoteEntry.TABLE_NAME, selection, selectionArgs);
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    if (selection == null || rowsDeleted != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsDeleted;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = mNotesDbHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsUpdated;

    switch (match) {
      case NOTE_LIST:
        rowsUpdated = db.update(NotesPersistenceContract.NoteEntry.TABLE_NAME, values, selection,
                                selectionArgs
                               );
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    if (rowsUpdated != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsUpdated;
  }
}
