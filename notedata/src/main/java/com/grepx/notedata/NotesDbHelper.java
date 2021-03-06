package com.grepx.notedata;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDbHelper extends SQLiteOpenHelper {
  public static final int DATABASE_VERSION = 4;

  public static final String DATABASE_NAME = "Notes.db";

  private static final String TEXT_TYPE = " TEXT";

  private static final String INTEGER_TYPE = " INTEGER";

  private static final String SQL_CREATE_ENTRIES =
      "CREATE TABLE " + NotesPersistenceContract.NoteEntry.TABLE_NAME + " (" +
      NotesPersistenceContract.NoteEntry._ID + INTEGER_TYPE + " PRIMARY KEY," +
      NotesPersistenceContract.NoteEntry.COLUMN_NAME_TITLE + TEXT_TYPE + "," +
      NotesPersistenceContract.NoteEntry.COLUMN_NAME_BODY + TEXT_TYPE +
      " )";

  public NotesDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_CREATE_ENTRIES);

    // add some sample data
    db.insert(NotesPersistenceContract.NoteEntry.TABLE_NAME, null,
              createNote(SampleData.sampleNoteTitle1, SampleData.sampleNoteBody1));
    db.insert(NotesPersistenceContract.NoteEntry.TABLE_NAME, null,
              createNote(SampleData.sampleNoteTitle2, SampleData.sampleNoteBody2));
    db.insert(NotesPersistenceContract.NoteEntry.TABLE_NAME, null,
              createNote(SampleData.sampleNoteTitle3, SampleData.sampleNoteBody3));
    db.insert(NotesPersistenceContract.NoteEntry.TABLE_NAME, null,
              createNote(SampleData.sampleNoteTitle4, SampleData.sampleNoteBody4));
    db.insert(NotesPersistenceContract.NoteEntry.TABLE_NAME, null,
              createNote(SampleData.sampleNoteTitle5, SampleData.sampleNoteBody5));
  }

  private ContentValues createNote(String title, String body) {
    ContentValues values = new ContentValues();
    values.put(NotesPersistenceContract.NoteEntry.COLUMN_NAME_TITLE, title);
    values.put(NotesPersistenceContract.NoteEntry.COLUMN_NAME_BODY, body);
    return values;
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + NotesPersistenceContract.NoteEntry.TABLE_NAME);
    onCreate(db);
  }

  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Not required as at version 1
  }
}
