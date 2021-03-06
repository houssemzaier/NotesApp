package com.bravedroid.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bravedroid.notesapp.adapters.NotesRecyclerAdapter;
import com.bravedroid.notesapp.models.Note;
import com.bravedroid.notesapp.persistence.NoteRepository;
import com.bravedroid.notesapp.repository.NoteRepositoryInterface;
import com.bravedroid.notesapp.util.VerticalSpacingItemDecorator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NotesActivity extends AppCompatActivity implements NotesRecyclerAdapter.OnNoteListener, View.OnClickListener {
    final static String TAG = "NotesActivity";
    // ui component
    private RecyclerView mRecyclerView;

    // vars
    private List<Note> mNotes;
    private NotesRecyclerAdapter mNotesRecyclerAdapter;
    private NoteRepositoryInterface mNoteRepositoryInterfaceInterface;
    private NoteRepository mNoteRepository ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        mRecyclerView = findViewById(R.id.recyclerView);

        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(this);
        initToolbar();
        initRecyclerView();
        mNoteRepository = new NoteRepository(this);
        retrieveNotes();
        Log.d(TAG, "onCreate: thread :" + Thread.currentThread().getName());
        mNoteRepository.insertNoteTask(new Note());
    }

    private void retrieveNotes() {
        mNoteRepository.retreiveNoteTask().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (mNotes.size() > 0) {
                    mNotes.clear();
                }
                if (notes != null) {
                    mNotes.addAll(notes);
                }
                mNotesRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.notes_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Notes");
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new VerticalSpacingItemDecorator(10));
        mNoteRepositoryInterfaceInterface = ((NotesApp) getApplication()).getNoteRepositoryInterface();
        mNotes = mNoteRepositoryInterfaceInterface.getNotes();
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mNotesRecyclerAdapter = new NotesRecyclerAdapter(mNotes, this);
        mRecyclerView.setAdapter(mNotesRecyclerAdapter);
    }

    // TODO: 04/05/2019 change to lambda syntax
    @Override
    public void onNoteClick(int position) {
        Log.d(TAG, "onNoteClick: clicked" + position);
        Intent intent = new Intent(this, NoteDetailActivity.class);
        intent.putExtra("selected_note", mNotes.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, NoteDetailActivity.class);
        startActivity(intent);
    }

    private void deleteNote(Note note) {
        mNotes.remove(note);
        mNotesRecyclerAdapter.notifyDataSetChanged();
        mNoteRepository.deleteNote(note);
    }

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deleteNote(mNotes.get(viewHolder.getAdapterPosition()));
        }
    };
}
