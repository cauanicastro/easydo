package cmput301.cauni.easydo.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;

import cmput301.cauni.easydo.R;
import cmput301.cauni.easydo.bll.Mailer;
import cmput301.cauni.easydo.bll.TodoItem;
import cmput301.cauni.easydo.view.enums.TodoOption;

public class generalView extends Activity
{
    protected ListView listView;
    protected static Object actionM = null;
    protected static Context cntxt;

    protected void DisableControlsCAB()
    {
    }
    protected void EnableControlsCAB()
    {
    }

    protected int getMenuAction()
    {
        return 0;
    }

    public void showPopup(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if (actionM == null) {
                    switch (item.getItemId()) {
                        case R.id.switch_view:
                            Intent in = new Intent(cntxt, archivedView.class);
                            startActivity(in);
                            break;
                        case R.id.mail_all:
                            sendMail(Mailer.EmailAll(cntxt));
                            break;
                        case R.id.summary:
                            Intent in_ = new Intent(cntxt, summaryView.class);
                            startActivity(in_);
                            break;
                    }
                    return true;
                }
                return false;
            }
        });

        MenuInflater mInflater = popup.getMenuInflater();

        mInflater.inflate(R.menu.options_menu, popup.getMenu());
        popup.show();
    }

    public void showToast(String text)
    {
        Toast.makeText(cntxt,text,Toast.LENGTH_SHORT).show();
    }

    public void sendMail(Intent i)
    {
        startActivity(Intent.createChooser(i, "Send Email:"));
    }

    protected void ActivateActionMode()
    {
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int selItems = listView.getCheckedItemCount();
                if (selItems == 1)
                    mode.setSubtitle(getString(R.string.item_selected_single));
                else
                    mode.setSubtitle(selItems + " " + getString(R.string.item_selected_multi));
                ((TodoItem)listView.getAdapter().getItem(position)).setSelected(checked);
                ((ArrayAdapter<TodoItem>)listView.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.menu_mail:
                        TodoOption opt = ((TodoItem)listView.getAdapter().getItem(0)).isArchived() ? TodoOption.ARCHIVED : TodoOption.ACTIVE;
                        ArrayList<Integer> aux_ = new ArrayList<Integer>();
                        SparseBooleanArray checked_ = listView.getCheckedItemPositions();

                        for (int i = 0; i < listView.getAdapter().getCount(); i++)
                        {
                            if (checked_.get(i))
                            {
                                aux_.add(((TodoItem)listView.getAdapter().getItem(i)).getId());
                            }
                        }
                        sendMail(Mailer.EmailTodoList(cntxt, aux_,opt));
                        return true;
                    case R.id.menu_unarchive:
                    case R.id.menu_archive:
                        boolean archive = item.getItemId() == R.id.menu_archive;
                        ArrayList<Integer> aux = new ArrayList<Integer>();
                        SparseBooleanArray checked = listView.getCheckedItemPositions();

                        for (int i = 0; i < listView.getAdapter().getCount(); i++)
                        {
                            if (checked.get(i))
                            {
                                aux.add(((TodoItem)listView.getAdapter().getItem(i)).getId());
                            }
                        }
                        TodoItem.HandleArchive(getApplicationContext(),aux,archive);
                        showToast(listView.getCheckedItemCount() + " " + getString(archive ? R.string.list_updated_archive : R.string.list_updated_unarchive));
                        mode.finish();
                        return true;
                    case R.id.menu_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(cntxt);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                TodoOption opt = ((TodoItem)listView.getAdapter().getItem(0)).isArchived() ? TodoOption.ARCHIVED : TodoOption.ACTIVE;
                                ArrayList<Integer> aux = new ArrayList<Integer>();
                                SparseBooleanArray checked = listView.getCheckedItemPositions();

                                for (int i = 0; i < listView.getAdapter().getCount(); i++)
                                {
                                    if (checked.get(i))
                                    {
                                        aux.add(((TodoItem)listView.getAdapter().getItem(i)).getId());
                                    }
                                }
                                TodoItem.HandleDelete(getApplicationContext(), aux, opt);
                                showToast(listView.getCheckedItemCount() + " " + getString(R.string.list_updated_delete));
                                ((ActionMode)actionM).finish();
                                UpdateList();
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                return;
                            }
                        });
                        builder.setTitle(R.string.ask_for_deletion).setMessage(R.string.deletion_message);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                mode.setTitle("Select items");
                mode.setSubtitle("1 item selected");
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(getMenuAction(), menu);
                actionM = mode;
                DisableControlsCAB();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {
                ArrayAdapter<TodoItem> aux = (ArrayAdapter<TodoItem>)listView.getAdapter();
                actionM = null;

                SparseBooleanArray checked = listView.getCheckedItemPositions();

                for (int i = 0; i < listView.getAdapter().getCount(); i++)
                {
                    if (checked.get(i))
                    {
                        aux.getItem(i).setSelected(false);
                    }
                }
                EnableControlsCAB();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        UpdateList();
        RecoverUserText();
    }

    @Override
    protected void onPause()
    {
        SaveUserText();
        super.onPause();
    }

    protected void UpdateList()
    {
    }

    protected void SaveUserText()
    {
    }

    protected void RecoverUserText()
    {
    }
}