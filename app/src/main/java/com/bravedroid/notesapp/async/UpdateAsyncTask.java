package com.bravedroid.notesapp.async;

import android.os.AsyncTask;
import android.util.Log;

import com.bravedroid.notesapp.models.Note;
import com.bravedroid.notesapp.persistence.NoteDao;

public class UpdateAsyncTask extends AsyncTask<Note, Void, Void> {
    private static final String TAG = "InsertAsyncTask";

    private NoteDao mNoteDao;

    public UpdateAsyncTask(NoteDao dao) {
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        Log.d(TAG, "doInBackground: "+ Thread.currentThread().getName());
        mNoteDao.update(notes);
        return null;
    }
}
