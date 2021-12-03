package com.example.todofinal;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Adapter class of the recycler view that displays the to do's as a list
 */
public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_STANDARD = 1;
    private final int VIEW_TYPE_COMPLETED = 2;
    private final List<Todo> todoItems;
    private final MainActivity activity;

    /**
     * Constructor for adapter
     *
     * @param todoItems  the list of all to do's to be displayed
     * @param activity   main activity
     * @param themeColor theme color (WIP)
     */
    public TodoAdapter(List<Todo> todoItems, MainActivity activity, String themeColor) {
        this.todoItems = todoItems;
        this.activity = activity;
        //WIP themeColor
    }

    /**
     * Gets the current to do item's type
     * An item can be completed or not completed
     *
     * @param position position of current to do item
     * @return to do type
     */
    @Override
    public int getItemViewType(int position) {
        if (todoItems.get(position).isCompleted()) {
            return VIEW_TYPE_COMPLETED;
        } else {
            return VIEW_TYPE_STANDARD;
        }
    }

    /**
     * Creates a ViewHolder based on if the to do is completed or not
     *
     * @param parent ViewGroup
     * @param viewType standard or completed View Type
     * @return standard or completed ViewHolder
     */
    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
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
        assert viewHolder != null;
        return viewHolder;
    }

    /**
     * Binds the View Holder based on its' type
     * If type is Standard the to do name is written not striked through and checkbox is not checked
     * If type is Completed the to do name is written striked through and checkbox is checked
     *
     * @param holder   the View Holder of the RecyclerView
     * @param position the position of the current to do item
     */
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

    /**
     * Gets the size of all to do items
     *
     * @return size of to do list
     */
    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    //TODO: Lägga till cardview

    /**
     * ViewHolder that applies to uncompleted to dos
     */
    public class ViewHolderStandard extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private final TextView name;
        private final CheckBox checkBox;

        /**
         * Constructor that sets a ClickListener on the checkbox so that a checked to do item will be marked as the correct type
         *
         * @param view View
         */
        public ViewHolderStandard(View view) {
            super(view);
            name = itemView.findViewById(R.id.toDoItem);
            checkBox = itemView.findViewById(R.id.checkBox);
            name.setOnLongClickListener(this);
            checkBox.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                Todo todo = todoItems.get(position);
                todo.setCompleted(checkBox.isChecked());
                activity.updateTodoAfterChecked(todo);
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

        /**
         * If a user presses long on a non completed to do it will call a method to edit it
         *
         * @param view View
         * @return true
         */
        @Override
        public boolean onLongClick(View view) {
            Todo previous = todoItems.get(getAbsoluteAdapterPosition());
            activity.editTodo(previous);
            return true;
        }
    }

    /**
     * ViewHolder that applies to completed to dos
     */
    class ViewHolderCompleted extends RecyclerView.ViewHolder {
        private final TextView name;
        private final CheckBox checkBox;

        /**
         * Constructor that sets a ClickListener on the checkbox so that a checked to do item will be marked as the correct type
         *
         * @param itemView View
         */
        public ViewHolderCompleted(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.toDoItem);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                Todo todo = todoItems.get(position);
                todo.setCompleted(checkBox.isChecked());
                activity.updateTodoAfterChecked(todo);
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
