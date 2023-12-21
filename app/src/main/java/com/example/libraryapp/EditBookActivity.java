package com.example.libraryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

public class EditBookActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_BOOK_TITLE = "pb.edu.pl.EDIT_BOOK_TITLE";
    public static final String EXTRA_EDIT_BOOK_AUTHOR= "pb.edu.pl.EDIT_BOOK_AUTHOR";
    public static final String EXTRA_EDIT_BOOK_ID ="pb.edu.pl.EDIT_BOOK_ID" ;
    private EditText editTitleEditText;
    private EditText editAuthorEditText;
    private int ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        editTitleEditText = findViewById(R.id.edit_book_title);
        editAuthorEditText = findViewById(R.id.edit_book_author);

        if (getIntent().hasExtra(EXTRA_EDIT_BOOK_TITLE)) {
            editAuthorEditText.setText(getIntent().getStringExtra(EXTRA_EDIT_BOOK_TITLE));
            editTitleEditText.setText(getIntent().getStringExtra(EXTRA_EDIT_BOOK_AUTHOR));

            ID = getIntent().getIntExtra(EXTRA_EDIT_BOOK_ID,0);
        }

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(editTitleEditText.getText())
                    || TextUtils.isEmpty(editAuthorEditText.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {

                String title = editTitleEditText.getText().toString();
                replyIntent.putExtra(EXTRA_EDIT_BOOK_TITLE, title);
                String author = editAuthorEditText.getText().toString();
                replyIntent.putExtra(EXTRA_EDIT_BOOK_AUTHOR, author);
                replyIntent.putExtra(EXTRA_EDIT_BOOK_ID, ID);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}