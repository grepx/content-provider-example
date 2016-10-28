package com.grepx.searchablenotes;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new SearchAdapter());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private class SearchAdapter extends RecyclerView.Adapter<NoteHolder> {

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      final View view = inflater.inflate(R.layout.note, parent, false);
      return new NoteHolder(view);
    }

    @Override public void onBindViewHolder(NoteHolder holder, int position) {
      holder.bind("id" + position, "note " + position);
    }

    @Override public int getItemCount() {
      return 5;
    }
  }

  private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private String id;
    final private TextView textView;

    public NoteHolder(View view) {
      super(view);
      textView = (TextView) view.findViewById(R.id.note_text);
      view.setOnClickListener(this);
    }

    public void bind(String id, String text) {
      textView.setText(text);
    }

    @Override
    public void onClick(View v) {
    }
  }
}
