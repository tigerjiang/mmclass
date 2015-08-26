
package com.multimedia.room.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.R;

public class SelfDictionaryFragment extends BaseFragment implements OnClickListener, TextWatcher {

    private Button mSearchBtn;
    private TextView mResultView;

    private final String DATABASE_PATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/dictionary";
    private AutoCompleteTextView mSearchWordText;
    private final String DATABASE_FILENAME = "dictionary.db";
    private SQLiteDatabase database;

    public static SelfDictionaryFragment newInstance() {
        SelfDictionaryFragment fragment = new SelfDictionaryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.self_dictionary_layout, null);
        database = openDatabase();
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        mSearchWordText = (AutoCompleteTextView) rootView.findViewById(R.id.seacth_word);
        mSearchWordText.addTextChangedListener(this);
        mSearchBtn = (Button) rootView.findViewById(R.id.search_button);
        mSearchBtn.setOnClickListener(this);
        mResultView = (TextView) rootView.findViewById(R.id.result_view);
    }

    public class DictionaryAdapter extends CursorAdapter
    {
        private LayoutInflater layoutInflater;

        @Override
        public CharSequence convertToString(Cursor cursor)
        {
            return cursor == null ? "" : cursor.getString(cursor
                    .getColumnIndex("_id"));
        }

        private void setView(View view, Cursor cursor)
        {
            TextView tvWordItem = (TextView) view;
            tvWordItem.setText(cursor.getString(cursor.getColumnIndex("_id")));
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
            setView(view, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent)
        {
            View view = layoutInflater.inflate(R.layout.word_list_item, null);
            setView(view, cursor);
            return view;
        }

        public DictionaryAdapter(Context context, Cursor c, boolean autoRequery)
        {
            super(context, c, autoRequery);
            layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

        Cursor cursor = database.rawQuery(
                "select english as _id from t_words where english like ?",
                new String[]
                {
                    s.toString() + "%"
                });
        DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this.getActivity(), cursor,
                true);
        mSearchWordText.setAdapter(dictionaryAdapter);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View view)
    {
        String sql = "select chinese from t_words where english=?";
        Cursor cursor = database.rawQuery(sql, new String[]
        {
            mSearchWordText.getText().toString()
        });
        String result = "";
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("chinese"));
        }
        mResultView.setText(result);
    }

    private SQLiteDatabase openDatabase()
    {
        try
        {
            String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            if (!dir.exists())
                dir.mkdir();
            if (!(new File(databaseFilename)).exists())
            {
                InputStream is = getResources().openRawResource(
                        R.raw.dictionary);
                FileOutputStream fos = new FileOutputStream(databaseFilename);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = is.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                is.close();
            }
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
                    databaseFilename, null);
            return database;
        } catch (Exception e)
        {
        }
        return null;
    }
    @Override
	public void onDetach() {
		super.onDetach();
	}
}
