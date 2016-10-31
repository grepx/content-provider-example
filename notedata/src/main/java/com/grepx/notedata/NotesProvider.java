package com.grepx.notedata;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;

public class NotesProvider extends ContentProvider {

  private static final int NOTE_LIST = 100;
  private static final int NOTE_ITEM = 101;
  private UriMatcher uriMatcher;
  private NotesDbHelper mNotesDbHelper;
  private NotesPersistenceContract contract;

  private UriMatcher buildUriMatcher() {
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = contract.contentAuthority;

    matcher.addURI(authority, NotesPersistenceContract.NoteEntry.TABLE_NAME, NOTE_LIST);
    matcher.addURI(authority, NotesPersistenceContract.NoteEntry.TABLE_NAME + "/*", NOTE_ITEM);

    return matcher;
  }

  @Override
  public boolean onCreate() {
    ProviderInfo providerInfo = getProviderInfo();
    String contentAuthority = providerInfo.authority;
    contract = new NotesPersistenceContract(contentAuthority);

    uriMatcher = buildUriMatcher();

    mNotesDbHelper = new NotesDbHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public String getType(Uri uri) {
    final int match = uriMatcher.match(uri);
    switch (match) {
      case NOTE_LIST:
        return contract.contentNoteListType;
      case NOTE_ITEM:
        return contract.contentNoteItemType;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  @Nullable
  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                      String sortOrder) {
    Cursor retCursor;
    switch (uriMatcher.match(uri)) {
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
    final int match = uriMatcher.match(uri);
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
            returnUri = contract.buildTasksUriWith(_id);
          } else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
          }
        } else {
          long _id = db.insert(NotesPersistenceContract.NoteEntry.TABLE_NAME, null, values);
          if (_id > 0) {
            returnUri = contract.buildTasksUriWith(_id);
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
    final int match = uriMatcher.match(uri);
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
    final int match = uriMatcher.match(uri);
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

  /**
   * Returns a {@link ProviderInfo} object for this provider.
   *
   * @return A {@link ProviderInfo} instance.
   * @throws RuntimeException if the provider can't be found in the given context.
   */
  @SuppressLint("NewApi")
  private ProviderInfo getProviderInfo() {
    Context context = getContext();
    PackageManager packageManager = context.getPackageManager();
    Class<?> providerClass = this.getClass();

    if (Build.VERSION.SDK_INT <= 8) {
      // in Android 2.2 PackageManger.getProviderInfo doesn't exist. We need to find it ourselves.

      // First get the PackageInfo of this app.
      PackageInfo packageInfo;
      try {
        packageInfo = packageManager.getPackageInfo(context.getPackageName(),
                                                    PackageManager.GET_META_DATA
                                                    | PackageManager.GET_PROVIDERS);
      } catch (PackageManager.NameNotFoundException e) {
        throw new RuntimeException("Could not find Provider!", e);
      }

      // next scan all providers for this class
      for (ProviderInfo provider : packageInfo.providers) {
        try {
          Class<?> providerInfoClass = Class.forName(provider.name);
          if (providerInfoClass.equals(providerClass)) {
            // We've finally found to ourselves! Isn't that a good feeling?
            return provider;
          }
        } catch (ClassNotFoundException e) {
          throw new RuntimeException("Missing provider class '" + provider.name + "'");
        }
      }

      // We got lost somewhere, no provider matched!?
      throw new RuntimeException("Could not find Provider!");
    }

    // On Android 2.3+ we just call the appropriate method
    try {
      return packageManager.getProviderInfo(new ComponentName(context, providerClass),
                                            PackageManager.GET_META_DATA);
    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException("Could not find Provider!", e);
    }
  }
}
