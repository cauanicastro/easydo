package cmput301.cauni.easydo.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cmput301.cauni.easydo.R;
import cmput301.cauni.easydo.bll.TodoItem;
import cmput301.cauni.easydo.view.enums.TodoOption;

public class archivedView extends generalView
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archived_view_fragment);
        this.listView = (ListView) findViewById(R.id.todoItems_list);
        this.cntxt = this;
        ActivateActionMode();
    }

    @Override
    protected int getMenuAction()
    {
        return R.menu.context_archive;
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
        ArrayAdapter<TodoItem> listAdapter = new TodoAdapter(this, TodoItem.getList(this, TodoOption.ARCHIVED));
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
            TextView textView;

            if ( view == null )
            {
                view = inflater.inflate(R.layout.textview_row, null);
                textView = (TextView) view.findViewById( R.id.listTextView);
                view.setTag(new TodoViewHolder(textView));
            }
            else
            {
                TodoViewHolder viewHolder = (TodoViewHolder)view.getTag();
                textView = viewHolder.getTextView();
            }
            textView.setText(todoItem.getTask());

            if (todoItem.getSelected())
                ((RelativeLayout)textView.getParent()).setBackgroundColor(Color.parseColor("#4169E1"));
            else
                ((RelativeLayout)textView.getParent()).setBackgroundColor(Color.TRANSPARENT);
            return view;
        }
    }

    protected static class TodoViewHolder {
        private TextView textView ;
        public TodoViewHolder( TextView textView )
        {
            this.textView = textView ;
        }
        public TextView getTextView()
        {
            return textView;
        }
    }
}
