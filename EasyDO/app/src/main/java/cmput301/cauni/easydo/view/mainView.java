package cmput301.cauni.easydo.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cmput301.cauni.easydo.R;
import cmput301.cauni.easydo.bll.TodoItem;
import cmput301.cauni.easydo.view.enums.TodoOption;

public class mainView extends generalView
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view_fragment);
        this.listView = (ListView) findViewById(R.id.todoItems_list);
        this.cntxt = this;
        ActivateActionMode();
        UpdateTextView();
    }

    @Override
    protected int getMenuAction()
    {
        return R.menu.context_main;
    }

    private void UpdateTextView()
    {
        final EditText edittext = (EditText) findViewById(R.id.todoInput_etext);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    ((ImageButton)findViewById(R.id.submit_btn)).callOnClick();
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void DisableControlsCAB()
    {
        ((EditText)findViewById(R.id.todoInput_etext)).setEnabled(false);
        ((ImageButton)findViewById(R.id.submit_btn)).setEnabled(false);
    }

    @Override
    protected void EnableControlsCAB()
    {
        ((EditText)findViewById(R.id.todoInput_etext)).setEnabled(true);
        ((ImageButton)findViewById(R.id.submit_btn)).setEnabled(true);
    }

    public void AddTodo(View view)
    {
        String inputText = ((EditText) findViewById(R.id.todoInput_etext)).getText().toString().trim();
        if (!TextUtils.isEmpty(inputText))
        {
            TodoItem aux = new TodoItem(this, inputText);
            ((EditText) findViewById(R.id.todoInput_etext)).setText("");
            UpdateList(false);
        }
    }

    public void UpdateView(View view)
    {
        UpdateList();
    }

    protected void UpdateList(boolean rememberListPos)
    {
        int listPos = listView.getFirstVisiblePosition();
        View view = listView.getChildAt(0);
        int top = (view == null) ? 0 : view.getTop();
        ArrayAdapter<TodoItem> listAdapter = new TodoAdapter(this, TodoItem.getList(this, TodoOption.ACTIVE));
        listView.setAdapter(listAdapter);
        if (rememberListPos)
        {
            listView.setSelectionFromTop(listPos, top);
            listView.requestFocus();
        }
    }

    @Override
    protected void UpdateList()
    {
        UpdateList(true);
    }

    @Override
    protected void SaveUserText()
    {
        TodoItem.SaveUserText(this, ((EditText) findViewById(R.id.todoInput_etext)).getText().toString().trim());
    }

    @Override
    protected void RecoverUserText()
    {
        ((EditText) findViewById(R.id.todoInput_etext)).setText(TodoItem.RecoverUserText(this));
    }

    protected static class TodoAdapter extends ArrayAdapter<TodoItem>
    {
        private LayoutInflater inflater;

        public TodoAdapter( Context context, List<TodoItem> todos)
        {
            super( context, R.layout.checkbox_textview_row, R.id.listTextView, todos );
            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int id, View view, ViewGroup parent)
        {
            // Todo item to display
            TodoItem todoItem = (TodoItem)this.getItem(id);
            // The child views in each row.
            CheckBox checkBox;
            TextView textView;

            // Create a new row for the list
            if ( view == null )
            {
                view = inflater.inflate(R.layout.checkbox_textview_row, null);

                textView = (TextView) view.findViewById( R.id.listTextView);
                checkBox = (CheckBox) view.findViewById( R.id.listCheckBox);

                // ~~ Nice Optimization Hint Used
                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                view.setTag(new TodoViewHolder(textView, checkBox));
                checkBox.setOnClickListener(
                    new View.OnClickListener()
                     {
                         public void onClick(View v)
                         {
                             CheckBox cb = (CheckBox) v;
                             TodoItem obj = (TodoItem) cb.getTag();
                             obj.isCompleted(getContext(), cb.isChecked(), ((ListView) v.getRootView().findViewById(R.id.todoItems_list)).getPositionForView(v));
                             ((Button) v.getRootView().findViewById(R.id.update_list_btn)).callOnClick();
                         }
                     }
                );
            }
            else
            {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                TodoViewHolder viewHolder = (TodoViewHolder)view.getTag();
                checkBox = viewHolder.getCheckBox();
                textView = viewHolder.getTextView();
            }

            checkBox.setTag(todoItem);

            checkBox.setChecked(todoItem.isCompleted());
            textView.setText(todoItem.getTask());

            if (todoItem.getSelected())
                ((RelativeLayout)checkBox.getParent()).setBackgroundColor(Color.parseColor("#4169E1"));
            else
                ((RelativeLayout)checkBox.getParent()).setBackgroundColor(Color.TRANSPARENT);

            if (actionM != null)
                checkBox.setVisibility(View.GONE);
            else
                checkBox.setVisibility(View.VISIBLE);

            return view;
        }
    }

    /** Holds child views for one row. */
    protected static class TodoViewHolder {
        private CheckBox checkBox ;
        private TextView textView ;
        public TodoViewHolder( TextView textView, CheckBox checkBox ) {
            this.checkBox = checkBox ;
            this.textView = textView ;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public TextView getTextView() {
            return textView;
        }
    }
}
