package com.example.todofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//TODO: Notifications och tid
//TODO: flytta.....
//TODO: inte vara på i viloläge
//TODO: Kunna ta bort och byta namn på listor, Ha listor som en egen databas, så inte alla inlägg ligger i samma
//TODO: undo efter delete

/**
 * Main activity for a to do list that displays all the to dos
 * Uses a Room database to store to do items
 */
public class MainActivity extends AppCompatActivity {

    private TodoAdapter toDoAdapter;
    private List<Todo> todoItems;
    private List<String> categories;
    private RecyclerView recyclerView;
    private ImageView emptyImage;
    private AppDatabase todoDatabase;
    private String tabName;
    private TabLayout tab;
    private Dialog dialog;
    private int completedTodos;
    private String themeColor; //WIP. TODO: sätt standard till pink
    //private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.my_toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");
        todoItems = new ArrayList<>();
        createRecyclerView();
        todoDatabase = AppDatabase.getDatabase(this);
        if (todoItems != null)
            todoItems.clear();
        loadTodos();
        loadCategories();
        setFabClickListeners();
        setBackgroundImage();
        filterByCategory("Today");
        setTabLayout();
        //countCompletedTodos();
    }

    /**
     * Initializes the recycler view
     */
    private void createRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        toDoAdapter = new TodoAdapter(todoItems, this, themeColor);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(toDoAdapter);
    }

    /**
     * Loads to do items from the database
     */
    @SuppressLint("StaticFieldLeak")
    private void loadTodos() {
        new AsyncTask<Void, Void, List<Todo>>() {

            /**
             * Returns a list of loaded to do items from the Today (standard) category
             *
             * @param params parameter
             * @return list of to do items
             */
            @Override
            protected List<Todo> doInBackground(Void... params) {
                return todoDatabase.getTodoDao().getAllByCategory("Today");
            }

            /**
             * Adapter is created with loaded to dos
             * @param todos list of to do items
             */
            @Override
            protected void onPostExecute(List<Todo> todos) {
                if (todoItems != null)
                    todoItems.clear();
                todoItems = todos;
                toDoAdapter = new TodoAdapter(todoItems, MainActivity.this, themeColor);
                recyclerView.setAdapter(toDoAdapter);
            }
        }.execute();
    }

    /**
     * Loads all categories from database
     */
    private void loadCategories() {
        List<String> list = todoDatabase.getTodoDao().getAllCategories();
        for (String category : list) {
            if (category.equals("Today")) {
                Collections.swap(list, 0, list.indexOf(category));
            }
        }
        categories = new ArrayList<>(list);
    }

    /**
     * Sets a click listener on add button
     */
    private void setFabClickListeners() {
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> addToDo());
    }

    //Todo: fixa så metoden kan hantera när en lista är tom

    /**
     * Shows a background image if to do category is empty
     */
    private void setBackgroundImage() {
        emptyImage = findViewById(R.id.imageView);
        if (todoItems.isEmpty()) {
            emptyImage.setVisibility(View.VISIBLE);
        } else {
            emptyImage.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Filters all the to do items showing only the items from a specific category
     *
     * @param category the name of the category
     */
    public void filterByCategory(String category) {
        List<Todo> list = todoDatabase.getTodoDao().getAllByCategory(category);
        todoItems.clear();
        todoItems.addAll(list);
        toDoAdapter.notifyDataSetChanged();
        setBackgroundImage();
    }

    /**
     * Set to do lists as tabs
     */
    private void setTabLayout() {
        tab = findViewById(R.id.tabs);
        tab.addOnTabSelectedListener(new Tabs());

        if (categories.isEmpty()) {
            tab.addTab(tab.newTab().setText("Today"));
        } else if (!categories.contains("Today")) {
            tab.addTab(tab.newTab().setText("Today"));
            for (String category : categories) {
                tab.addTab(tab.newTab().setText(category));
            }
        } else {
            for (String category : categories) {
                tab.addTab(tab.newTab().setText(category));
            }
        }
        tabName = "Today";
        tab.getTabAt(0).select();
    }

    /**
     * Saves current theme choice if app is paused (WIP)
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("theme", themeColor);
        myEdit.apply();
    }

    /**
     * Creates a main menu
     *
     * @param menu menu
     * @return true if menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles clicks on main menu items
     *
     * @param item the selected main menu item
     * @return true if item selected
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String text = item.getTitle().toString();
        if (text.equals("menu_addcategory")) {
            newCategory();
        } else if (text.equals("menu_delete")) {
            clearAll();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
//        String input = sh.getString("username", "");
//        username = input;
//        TextView textView = findViewById(R.id.textView4);
//        if (textView == null){
//            textView.setText("Good day!");
//        } else {
//            textView.setText("Good day, " + username + "!");
//        }

//        String input = sh.getString("theme", "");
//        switch (input){
//            case "green":
//                //setGreenTheme();
//                setPinkTheme();
//                break;
//            case "blue":
//                setBlueTheme();
//                break;
//            case "pink":
//                setPinkTheme();
//                break;
//        }
    //}

    /**
     * Counts completed to do items
     */
    private void countCompletedTodos() {
        completedTodos = 0;
        for (Todo todo : todoItems) {
            if (todo.isCompleted()) {
                completedTodos++;
            }
        }
        //int totalTodos = todoItems.size();
        //TextView textView = findViewById(R.id.completed_todos_text);
        //textView.setText(completedTodos + "/" + totalTodos + " complete");
    }

    /**
     * Starts a new settings activity
     *
     * @param item MenuItem
     */
    public void startSettingsActivity(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Shows a dialog with celebratory text after the user completes all to dos in a category
     */
    private void openCompletionDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.completion_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * Dialog which lets the user add a new to do
     */
    private void addToDo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setMessage("Add to do")
                .setPositiveButton("Add", (dialog, id) -> {
                    if (input.getText().toString().matches("")) {
                        Toast.makeText(MainActivity.this, "To do can't be empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        Todo todo = new Todo();
                        todo.name = input.getText().toString();
                        todo.listName = tabName;
                        todo.completed = false;
                        todoDatabase.getTodoDao().insertAll(todo);
                        todoItems.add(todo);
                        toDoAdapter.notifyDataSetChanged();
                        emptyImage.setVisibility(View.GONE);
                        //countCompletedTodos();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Dialog which lets the user edit a to do
     *
     * @param todo the to do to be edited
     */
    public void editTodo(Todo todo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        input.setText(todo.name);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setMessage("Edit to do")
                .setPositiveButton("Done", (dialog, id) -> {
                    if (input.getText().toString().matches("")) {
                        Toast.makeText(this, "To do can't be empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        todo.setName(input.getText().toString());
                        todoDatabase.getTodoDao().update(todo);
                        toDoAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Removes all completed to dos in the current category
     */
    private void clearAll() {
        Iterator<Todo> toDoItemsIterator = todoItems.iterator();
        while (toDoItemsIterator.hasNext()) {
            Todo item = toDoItemsIterator.next();
            if (item.completed) {
                toDoItemsIterator.remove();
                todoDatabase.getTodoDao().delete(item);
            }
        }
        toDoAdapter.notifyDataSetChanged();
        if (todoItems.isEmpty()) {
            emptyImage.setVisibility(View.VISIBLE);
        }
        //countCompletedTodos();
    }

    /**
     * Method that is called after a to do item is checked as completed or uncompleted
     * Updates the to do in database and checks if all to do items in a category has been completed
     *
     * @param todo to do that has been checked
     */
    public void updateTodoAfterChecked(Todo todo) {
        todoDatabase.getTodoDao().update(todo);
        countCompletedTodos();
        if (completedTodos == todoItems.size()) {
            openCompletionDialog();
        }
    }

    /**
     * Dialog that lets the user create a new category
     */
    private void newCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setMessage("Add new category")
                .setPositiveButton("Add", (dialog, id) -> {
                    if (input.getText().toString().matches("")) {
                        Toast.makeText(MainActivity.this, "Category can't be empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        filterByCategory(input.getText().toString());
                        categories.add(input.getText().toString());
                        emptyImage.setVisibility(View.VISIBLE);
                        tab.addTab(tab.newTab().setText(input.getText().toString()));
                        tab.getTabAt(tab.getTabCount() - 1).select();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * ItemTouchHelper to enable moving and swiping to do items
     */
    private final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT) {

        /**
         * Lets the user move a to do item up and down. (WIP)
         *
         * @param recyclerView The RecyclerView
         * @param viewHolder The RecyclewView's ViewHolder
         * @param target the position where the to do is moved to
         * @return true
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAbsoluteAdapterPosition();
            int toPosition = target.getAbsoluteAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(todoItems, i, i + 1);
                }

            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(todoItems, i, i - 1);
                }
            }
            toDoAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        /**
         * Lets the user swipe a to do item from right to left to remove it
         *
         * @param viewHolder RecyclerView ViewHolder
         * @param direction the direction of swipe
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAbsoluteAdapterPosition();
            Todo item = todoItems.get(position);
            todoItems.remove(position);
            toDoAdapter.notifyItemRemoved(position);
            toDoAdapter.notifyItemRangeChanged(position, toDoAdapter.getItemCount());
            todoDatabase.getTodoDao().delete(item);
            setBackgroundImage();
            //countCompletedTodos();
        }

        /**
         * Handles swiping of to do items
         * Shows a delete icon when user removes a to do item by swiping from right to left
         *
         * @param c canvas
         * @param recyclerView RecyclerView
         * @param viewHolder ViewHolder
         * @param dX x-coordinate
         * @param dY y-coordinate
         * @param actionState action state
         * @param isCurrentlyActive is currently active
         */
        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            ColorDrawable deleteBackground = new ColorDrawable(Color.rgb(255, 223, 239));
            Drawable deleteIcon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_baseline_delete_24);

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;

            if (dX > 0) { // Swiping to the right
                //WIP
            } else if (dX < 0) { // Swiping to the left
                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteBackground.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
                deleteBackground.draw(c);
                deleteIcon.draw(c);
            } else { // view is unSwiped
                deleteBackground.setBounds(0, 0, 0, 0);
            }
        }
    };

    /**
     * Class that handles category tabs
     */
    class Tabs implements TabLayout.OnTabSelectedListener {

        /**
         * When a tab is selected the selected category with its to do items will appear
         *
         * @param tab tab that is selected
         */
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            filterByCategory(tab.getText().toString());
            tabName = tab.getText().toString();
            //countCompletedTodos();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

//    public void setGreenTheme() {
//        themeColor = "green";
//
//        //Bakgrund
//        constraintLayout.setBackgroundResource(R.color.green_background);
//        appBarLayout.setBackgroundResource(R.color.green_background);
//
//        //FAB
//        int color = Color.rgb(175, 231, 203);
//        button.setBackgroundTintList(ColorStateList.valueOf(color));
//
//        //Tabs färg
//        tabLayout.setSelectedTabIndicatorColor(color);
//
//        //Logga
//        imageView.setImageResource(R.mipmap.background);
//
//        toDoAdapter.notifyDataSetChanged();
//        //Todos bakgrund
//
//    }
//
//    public void setBlueTheme() {
//        themeColor = "blue";
//        TextView textView = findViewById(R.id.textView);
//        textView.setTextColor(Color.BLUE);
//
//        constraintLayout.setBackgroundResource(R.color.catpink);
//
//        toDoAdapter.notifyDataSetChanged();
//    }
//
//    public void setPinkTheme() {
//        themeColor = "pink";
//
//        //Bakgrund
//        constraintLayout.setBackgroundResource(R.color.rv_backgroundbackground);
//        appBarLayout.setBackgroundResource(R.color.rv_backgroundbackground);
//
//        //FAB
//        int color = Color.rgb(251, 184, 234);
//        button.setBackgroundTintList(ColorStateList.valueOf(color));
//
//        //Tabs färg
//        tabLayout.setSelectedTabIndicatorColor(color);
//
//        //Logga
//        imageView.setImageResource(R.drawable.cat);
//
//        toDoAdapter.notifyDataSetChanged();
//    }
}
