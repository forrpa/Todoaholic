package com.example.todofinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_STANDARD = 1;
    private final int VIEW_TYPE_COMPLETED = 2;
    private final List<Todo> todoItems;
    private final MainActivity activity;
    private final String themeColor;

    public TodoAdapter(List<Todo> dataSet, MainActivity activity, String themeColor) {
        todoItems = dataSet;
        this.activity = activity;
        this.themeColor = themeColor;
    }

    @Override
    public int getItemViewType(int position) {
        if (todoItems.get(position).isCompleted()) {
            return VIEW_TYPE_COMPLETED;
        } else {
            return VIEW_TYPE_STANDARD;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case VIEW_TYPE_STANDARD:
                View standardView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todo_item_standard, parent, false);
                viewHolder = new ViewHolderStandard(standardView);
                break;
            case VIEW_TYPE_COMPLETED:
                View completedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todo_item_completed, parent, false);
                viewHolder = new ViewHolderCompleted(completedView);
                break;
            default:
                viewHolder = null;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        Todo item = todoItems.get(position);
        if (viewType == VIEW_TYPE_STANDARD) {
            TextView nameView = ((ViewHolderStandard) holder).name;
            nameView.setPaintFlags(nameView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            nameView.setText(item.name);
            CheckBox checkBox = ((ViewHolderStandard) holder).checkBox;
            checkBox.setChecked(false);
        } else if (viewType == VIEW_TYPE_COMPLETED) {
            TextView nameView = ((ViewHolderCompleted) holder).name;
            nameView.setText(item.name);
            nameView.setPaintFlags(nameView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            CheckBox checkBox = ((ViewHolderCompleted) holder).checkBox;
            checkBox.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    //TODO: ta reda på hur jag lägger till cardview
    public class ViewHolderStandard extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private final TextView name;
        private final CheckBox checkBox;

        public ViewHolderStandard(View view) {
            super(view);
            name = itemView.findViewById(R.id.toDoItem);
            checkBox = itemView.findViewById(R.id.checkBox);
            name.setOnLongClickListener(this);
            checkBox.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                Todo todo = todoItems.get(position);
                todo.setCompleted(checkBox.isChecked());
                activity.setTodoCompleted(todo);
                notifyDataSetChanged();
            });

/*            switch(themeColor){
                case "green":
                    int color = Color.rgb(175, 231, 203);
                    checkBox.setButtonTintList(ColorStateList.valueOf(color));

                    break;
                case "pink":
                    int color2 = Color.rgb(251, 184, 234);
                    checkBox.setButtonTintList(ColorStateList.valueOf(color2));
                    break;
                case "blue":
                    int color3 = Color.rgb(0,255,255);
                    checkBox.setButtonTintList(ColorStateList.valueOf(color3));
                    break;
                default:
                    break;
            }*/
        }

        @Override
        public boolean onLongClick(View view) {
            Todo previous = todoItems.get(getAbsoluteAdapterPosition());
            activity.editTodo(previous);
            return true;
        }
    }

    class ViewHolderCompleted extends RecyclerView.ViewHolder {
        private final TextView name;
        private final CheckBox checkBox;

        public ViewHolderCompleted(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.toDoItem);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                Todo todo = todoItems.get(position);
                todo.setCompleted(checkBox.isChecked());
                activity.setTodoCompleted(todo);
                notifyDataSetChanged();
            });

/*            switch(themeColor){
                case "green":
                    int color = Color.rgb(175, 231, 203);
                    checkBox.setButtonTintList(ColorStateList.valueOf(color));
                    break;
                case "pink":
                    int color2 = Color.rgb(251, 184, 234);
                    checkBox.setButtonTintList(ColorStateList.valueOf(color2));
                    break;
                case "blue":
                    int color3 = Color.rgb(0,255,255);
                    checkBox.setButtonTintList(ColorStateList.valueOf(color3));
                    break;
                default:
                    break;
            }*/

        }
    }
}
