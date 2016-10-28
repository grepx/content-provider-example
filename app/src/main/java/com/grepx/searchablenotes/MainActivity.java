package com.grepx.searchablenotes;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.grepx.searchablenotes.data.NotesPersistenceContract;
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
        NotesPersistenceContract.NoteEntry.buildNotesUri(),
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
      int index = cursor.getColumnIndex(NotesPersistenceContract.NoteEntry.COLUMN_NAME_BODY);
      int id = cursor.getInt(index);

      index = cursor.getColumnIndex(NotesPersistenceContract.NoteEntry.COLUMN_NAME_BODY);
      String body = cursor.getString(index);

      viewHolder.bind(id, body);
    }
  }

  private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private int id;
    final private TextView textView;

    public NoteHolder(View view) {
      super(view);
      textView = (TextView) view.findViewById(R.id.note_text);
      view.setOnClickListener(this);
    }

    public void bind(int id, String body) {
      this.id = id;
      textView.setText(body);
    }

    @Override
    public void onClick(View v) {
    }
  }
}
