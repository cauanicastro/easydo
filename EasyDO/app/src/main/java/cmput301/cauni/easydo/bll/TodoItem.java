package cmput301.cauni.easydo.bll;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import cmput301.cauni.easydo.dal.TodoData;
import cmput301.cauni.easydo.view.enums.*;

public class TodoItem
{
    private Integer Id;
    private String Task;
    public boolean Archived;
    public boolean Completed;
    private Calendar CreatedAt;

    private boolean Selected;

    public TodoItem(Integer id, String task, boolean archived, boolean completed, Calendar createdAt)
    {
        this.Id = id;
        this.Task = task;
        this.Archived = archived;
        this.Completed = completed;
        this.CreatedAt = createdAt;
    }

    public TodoItem(Context c, String task)
    {
        this(TodoData.getTodoIdSeed(c), task, false, false, Calendar.getInstance());
        try
        {
            List<TodoItem> aux = getList(c, TodoOption.ACTIVE);
            aux.add(this);
            TodoData.SaveTodos(c, aux);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Integer getId()
    {
        return this.Id;
    }

    private void setId(int id)
    {
        this.Id = id;
    }

    public String getTask()
    {
        return this.Task;
    }

    public boolean isArchived()
    {
        return this.Archived;
    }

    public void isArchived(Context c, boolean archive)
    {
        TodoData.HandleArchive(c,
                Arrays.asList(this.getId()),
                archive);
    }

    public boolean isCompleted()
    {
        return this.Completed;
    }

    public void isCompleted(Context c, boolean completed, int pos)
    {
        TodoData.HandleCompleted(c,
                Arrays.asList(pos),
                completed);
    }

    public Calendar getDateCreated()
    {
        return this.CreatedAt;
    }

    public final static List<TodoItem> getList(Context c, TodoOption opt)
    {
        List<TodoItem> auxList = new ArrayList<TodoItem>();
        switch (opt)
        {
            case ACTIVE:
                auxList = TodoData.LoadTodos(c);
                break;
            case ARCHIVED:
                auxList = TodoData.LoadArchivedTodos(c);
                break;
        }
        return auxList;
    }

    private TodoOption getFolder()
    {
        return (this.isArchived() ? TodoOption.ARCHIVED : TodoOption.ACTIVE);
    }

    public final static void SaveUserText(Context c, String text)
    {
        TodoData.saveUserText(c, text);
    }

    public boolean getSelected()
    {
        return this.Selected;
    }

    public void setSelected(boolean opt)
    {
        this.Selected = opt;
    }

    public final static String RecoverUserText(Context c)
    {
        return TodoData.recoverUserText(c);
    }

    public final static void HandleArchive(Context c, ArrayList<Integer> idlist, boolean archive)
    {
        TodoData.HandleArchive(c,
                idlist,
                archive);
    }

    public final static void HandleDelete(Context c, ArrayList<Integer> pos, TodoOption opt)
    {
        TodoData.HandleDelete(c,
                opt,
                pos);
    }

    public final static HashMap<String,Integer> getSummary(Context c)
    {
        return TodoData.getSummary(c);
    }
}