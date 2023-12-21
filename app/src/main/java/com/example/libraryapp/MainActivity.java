package com.example.libraryapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int NEW_BOOK_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_BOOK_ACTIVITY_REQUEST_CODE = 2;
    public static final String EXTRA_EDIT_BOOK_TITLE = "pb.edu.pl.EDIT_BOOK_TITLE";
    public static final String EXTRA_EDIT_BOOK_AUTHOR= "pb.edu.pl.EDIT_BOOK_AUTHOR";
    public static final String EXTRA_EDIT_BOOK_ID= "pb.edu.pl.EDIT_BOOK_ID";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private BookViewModel bookViewModel;

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        bookViewModel.findAll().observe(this,adapter::setBooks);
        Log.d("mainActivity","Instance BookViewModel created");
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        FloatingActionButton addBookButton = findViewById(R.id.add_button);

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditBookActivity.class);
                startActivityForResult(intent, NEW_BOOK_ACTIVITY_REQUEST_CODE);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_BOOK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            Book book = new Book(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE),
                    data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR));
            bookViewModel.insert(book);
            Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.book_added), Snackbar.LENGTH_LONG).show();
        }else if(requestCode == EDIT_BOOK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK )
        {
            Book book = new Book(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE),
                    data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR));
            book.setId(data.getIntExtra(EditBookActivity.EXTRA_EDIT_BOOK_ID,-1));
            bookViewModel.update(book);
        }
        else{
            Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.empty_not_saved),
                    Snackbar.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final private TextView bookTitleTextView;
        final private TextView bookAuthorTextView;
        private Book book;
        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item, parent, false));
            book = new Book();
            bookTitleTextView = itemView.findViewById(R.id.title);
            bookAuthorTextView = itemView.findViewById(R.id.author);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Book book) {
            this.book.setId(book.getId());
            this.book.setAuthor(book.getAuthor());
            this.book.setTitle(book.getTitle());
            bookAuthorTextView.setText(book.getAuthor());
            bookTitleTextView.setText(book.getTitle());
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, EditBookActivity.class);
            intent.putExtra(EXTRA_EDIT_BOOK_TITLE, book.getTitle());
            intent.putExtra(EXTRA_EDIT_BOOK_AUTHOR, book.getAuthor());
            intent.putExtra(EXTRA_EDIT_BOOK_ID, book.getId());
            startActivityForResult(intent, EDIT_BOOK_ACTIVITY_REQUEST_CODE);
        }

        @Override
        public boolean onLongClick(View view) {
            bookViewModel.delete(this.book);
            Snackbar.make(itemView.getRootView(), "deleted_book_info", Snackbar.LENGTH_LONG)
                    .show();
            return true;
        }
    }
    private class BookAdapter extends RecyclerView.Adapter<BookHolder>{
        private List<Book> books;


        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(),parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            if(books!=null){
                Book book = books.get(position);
                holder.bind(book);
            }
            else{
                Log.d("MainActivity","no books");
            }
        }

        @Override
        public int getItemCount() {
            if(books!=null)
            {
                return books.size();
            }
            else {return 0;}

        }
        void setBooks(List<Book> books){
            this.books = books;
            notifyDataSetChanged();
        }
        private ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Book deletedBook = books.get(position);
                books.remove(position);
                notifyItemRemoved(position);

                Snackbar.make(viewHolder.itemView, "archivised_book_info", Snackbar.LENGTH_LONG)
                        .setAction("undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Opcjonalnie: Przywróć zarchiwizowaną książkę
                                books.add(position, deletedBook);
                                notifyItemInserted(position);
                            }
                        })
                        .show();
            }
        };

        private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }
}