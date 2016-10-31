package com.grepx.searchablenotes;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.grepx.notedata.NotesPersistenceContract;
import com.grepx.searchablenotes.util.CursorRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

  private RecyclerView recyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
      }
    });

    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    getSupportLoaderManager().initLoader(1, null, this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(
        getApplicationContext(),
        new NotesPersistenceContract("com.grepx.searchablenotespro.notedata").buildNotesUri(),
        NotesPersistenceContract.NoteEntry.NOTES_COLUMNS, null, null, null
    );
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    recyclerView.setAdapter(new SearchAdapter(this, cursor));
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {
  }

  private class SearchAdapter extends CursorRecyclerViewAdapter<NoteHolder> {

    public SearchAdapter(Context context, Cursor cursor) {
      super(context, cursor);
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      View view = inflater.inflate(R.layout.note, parent, false);
      return new NoteHolder(view);
    }

    @Override public void onBindViewHolder(NoteHolder viewHolder, Cursor cursor) {
      int index = cursor.getColumnIndex(NotesPersistenceContract.NoteEntry._ID);
      int id = cursor.getInt(index);

      index = cursor.getColumnIndex(NotesPersistenceContract.NoteEntry.COLUMN_NAME_TITLE);
      String title = cursor.getString(index);

      index = cursor.getColumnIndex(NotesPersistenceContract.NoteEntry.COLUMN_NAME_BODY);
      String body = cursor.getString(index);

      viewHolder.bind(id, title, body);
    }
  }

  private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private int id;
    final private TextView title;
    final private TextView body;

    public NoteHolder(View view) {
      super(view);
      title = (TextView) view.findViewById(R.id.note_title);
      body = (TextView) view.findViewById(R.id.note_body);
      view.setOnClickListener(this);
    }

    public void bind(int id, String title, String body) {
      this.id = id;
      this.title.setText(title);
      this.body.setText(body);
    }

    @Override
    public void onClick(View v) {
    }
  }
}
