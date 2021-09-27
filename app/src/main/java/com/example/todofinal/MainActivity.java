package com.example.todofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//TODO: Notifications och tid
//TODO: flytta.....
//TODO: inte vara på i viloläge
//TODO: Kunna ta bort och byta namn på listor, Ha listor som en egen databas, så inte alla inlägg ligger i samma
//TODO: undo efter delete
public class MainActivity extends AppCompatActivity {

    private TodoAdapter toDoAdapter;
    private List<Todo> todoItems;
    private List<String> listNames;
    private RecyclerView recyclerView;
    private ImageView emptyImage;
    private AppDatabase todoDatabase;
    private String tabName;
    private TabLayout tab;
    private Dialog dialog;
    private int completedTodos;
    private String themeColor; //TODO: sätt standard till pink
    private ConstraintLayout constraintLayout;
    private AppBarLayout appBarLayout;
    private FloatingActionButton button;
    private TabLayout tabLayout;
    private ImageView imageView;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.my_toolbar));
        getSupportActionBar().setTitle("");
        todoItems = new ArrayList<>();
        showRecyclerView();
        todoDatabase = AppDatabase.getDatabase(this);
        if (todoItems != null)
            todoItems.clear();
        loadTodos();
        loadAndInitializeLists();
        setClickListenersOnButtons();
        setBackgroundImage();
        filterByListName("Today");
        setTabLayout();
        calculateCompletedTodos();
        constraintLayout = findViewById(R.id.constraint_layout);
        appBarLayout = findViewById(R.id.appBarLayout);
        button = findViewById(R.id.fab_add);
        tabLayout = findViewById(R.id.tabs);
        imageView = findViewById(R.id.logo_img);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

//        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
//        String input = sh.getString("username", "");
//        username = input;
//        TextView textView = findViewById(R.id.textView4);
//        if (textView == null){
//            textView.setText("Good day!");
//        } else {
//            textView.setText("Good day, " + username + "!");
//        }

//        if (intent.getStringExtra("THEME_COLOR").toLowerCase().equals("green")) {
//            setGreenTheme();
//        } else if (intent.getStringExtra("THEME_COLOR").toLowerCase().equals("blue")) {
//            setBlueTheme();
//        } else if (intent.getStringExtra("THEME_COLOR").toLowerCase().equals("pink")) {
//            setPinkTheme();
//        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loadTodos() {
        new AsyncTask<Void, Void, List<Todo>>() {
            @Override
            protected List doInBackground(Void... params) {
                return todoDatabase.getTodoDao().getAllByListName("Today");
            }

            @Override
            protected void onPostExecute(List todos) {
                if (todoItems != null)
                    todoItems.clear();
                todoItems = todos;
                toDoAdapter = new TodoAdapter(todoItems, MainActivity.this, themeColor);
                recyclerView.setAdapter(toDoAdapter);
            }
        }.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("theme", themeColor);
        myEdit.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //main_menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //getMenuInflater().inflate(R.menu.main_menu, menu);
//        for (String item : listNames) {
//            if (!item.equals("Today")) {
//                menu.add(0, 0, menu.size() + 1, item);
//            }
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String text = item.getTitle().toString();
        if (text.equals("menu_addlist")) {
            newList();
        } else if (text.equals("menu_delete")) {
            clearAll();
        } else if (text.equals("menu_today")) {
            //startSettings();
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

    public void calculateCompletedTodos() {
        completedTodos = 0;
        for (Todo todo : todoItems) {
            if (todo.isCompleted()) {
                completedTodos++;
            }
        }
        int totalTodos = todoItems.size();
        //TextView textView = findViewById(R.id.completed_todos_text);
        //textView.setText(completedTodos + "/" + totalTodos + " complete");
    }

    public void startSettingsActivity(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openWinDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.win_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setTabLayout() {
        tab = findViewById(R.id.tabs);
        tab.addOnTabSelectedListener(new Tabs());
        if (listNames.isEmpty()) {
            tab.addTab(tab.newTab().setText("Today"));
        } else if (!listNames.contains("Today")){
            tab.addTab(tab.newTab().setText("Today"));
            for (String s : listNames) {
                tab.addTab(tab.newTab().setText(s));
            }
        } else {
            for (String s : listNames) {
                tab.addTab(tab.newTab().setText(s));
            }
        }
        tabName = "Today";
        tab.getTabAt(0).select();
    }

    //Todo: En metod för alla när det gäller att visa bilden
    private void setBackgroundImage() {
        emptyImage = findViewById(R.id.imageView);
        int rows = todoDatabase.getTodoDao().getRowCount();
        if (rows == 0) {
            emptyImage.setVisibility(View.VISIBLE);
        } else {
            emptyImage.setVisibility(View.GONE);
        }
    }

    private void loadAndInitializeLists() {
        List<String> list = todoDatabase.getTodoDao().getAllListNames();
        for (String name : list) {
            if (name.equals("Today")) {
                Collections.swap(list, 0, list.indexOf(name));
            }
        }
        listNames = new ArrayList<>(list);
    }

    private void setClickListenersOnButtons() {
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDo();
            }
        });
    }

    private void showRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        toDoAdapter = new TodoAdapter(todoItems, this, themeColor);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(toDoAdapter);
    }

    public void addToDo() {
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
                        //todor.listName = listNameTextView.getText().toString();
                        todo.listName = tabName;
                        todo.completed = false;
                        todoDatabase.getTodoDao().insertAll(todo);
                        todoItems.add(todo);
                        toDoAdapter.notifyDataSetChanged();
                        emptyImage.setVisibility(View.GONE);
                        calculateCompletedTodos();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

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

    public void clearAll() {
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
        calculateCompletedTodos();
    }

    public void filterByListName(String listName) {
        List<Todo> list = todoDatabase.getTodoDao().getAllByListName(listName);
        String s = "";
        for (Todo todo : list) {
            s += todo.name + "\n";
        }
        todoItems.clear();
        todoItems.addAll(list);
        toDoAdapter.notifyDataSetChanged();
        if (todoItems.isEmpty()) {
            emptyImage.setVisibility(View.VISIBLE);
        } else {
            emptyImage.setVisibility(View.INVISIBLE);
        }
    }

    public void setTodoCompleted(Todo todo) {
        todoDatabase.getTodoDao().update(todo);
        calculateCompletedTodos();
        if (completedTodos == todoItems.size()) {
            openWinDialog();
        }
    }

    public void newList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setMessage("Add new list")
                .setPositiveButton("Add", (dialog, id) -> {
                    if (input.getText().toString().matches("")) {
                        Toast.makeText(MainActivity.this, "List can't be empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        filterByListName(input.getText().toString());
                        listNames.add(input.getText().toString());
                        emptyImage.setVisibility(View.VISIBLE);
                        tab.addTab(tab.newTab().setText(input.getText().toString()));
                        tab.getTabAt(tab.getTabCount() - 1).select();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT) {


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
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

        @Override
        public void clearView(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
//            ArrayList<Todo> list = new ArrayList<>(todoDatabase.getTodoDao().getAllByListName(tabName));
//            //todoDatabase.getTodoDao().deleteAll(list); //Tar bort alla från databas, FUNKAR
//
//            //Todo set name
//            for (Todo t : todoItems) {
//                //todoDatabase.getTodoDao().insert(t); //Lägger till alla i databas i rätt ordning
//                todoDatabase.getTodoDao().update(t);
//            }
//            //filterByListName(tabName); //Visar att det inte flyttats
//            //toDoAdapter.notifyDataSetChanged();
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAbsoluteAdapterPosition();
            Todo item = todoItems.get(position);
            todoItems.remove(position);
            toDoAdapter.notifyItemRemoved(position);
            toDoAdapter.notifyItemRangeChanged(position, toDoAdapter.getItemCount());
            todoDatabase.getTodoDao().delete(item);
            if (todoItems.isEmpty()) {
                emptyImage.setVisibility(View.VISIBLE);
            }
            calculateCompletedTodos();
        }

        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            ColorDrawable background = new ColorDrawable(Color.rgb(255, 223, 239));
            ColorDrawable editBackground = new ColorDrawable(Color.WHITE);
            Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_baseline_delete_24);
            Drawable editIcon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_baseline_add_24);

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20; //20

            if (dX > 0) { // Swiping to the right
                int iconMargin = (itemView.getHeight() - editIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - editIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + editIcon.getIntrinsicHeight();

                int iconLeft = itemView.getLeft() + iconMargin + editIcon.getIntrinsicWidth();
                int iconRight = itemView.getLeft() + iconMargin;
                editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                editBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                        itemView.getBottom());
                editBackground.draw(c);
                editIcon.draw(c);

            } else if (dX < 0) { // Swiping to the left
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
                icon.draw(c);
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }
        }
    };

    class Tabs implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            filterByListName(tab.getText().toString());
            tabName = tab.getText().toString();
            calculateCompletedTodos();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    public void setGreenTheme() {
        themeColor = "green";

        //Bakgrund
        constraintLayout.setBackgroundResource(R.color.green_background);
        appBarLayout.setBackgroundResource(R.color.green_background);

        //FAB
        int color = Color.rgb(175, 231, 203);
        button.setBackgroundTintList(ColorStateList.valueOf(color));

        //Tabs färg
        tabLayout.setSelectedTabIndicatorColor(color);

        //Logga
        imageView.setImageResource(R.mipmap.background);

        toDoAdapter.notifyDataSetChanged();
        //Todos bakgrund

    }

    public void setBlueTheme() {
        themeColor = "blue";
        TextView textView = findViewById(R.id.textView);
        textView.setTextColor(Color.BLUE);

        constraintLayout.setBackgroundResource(R.color.catpink);

        toDoAdapter.notifyDataSetChanged();
    }

    public void setPinkTheme() {
        themeColor = "pink";

        //Bakgrund
        constraintLayout.setBackgroundResource(R.color.rv_backgroundbackground);
        appBarLayout.setBackgroundResource(R.color.rv_backgroundbackground);

        //FAB
        int color = Color.rgb(251, 184, 234);
        button.setBackgroundTintList(ColorStateList.valueOf(color));

        //Tabs färg
        tabLayout.setSelectedTabIndicatorColor(color);

        //Logga
        imageView.setImageResource(R.drawable.cat);

        toDoAdapter.notifyDataSetChanged();
    }
}
