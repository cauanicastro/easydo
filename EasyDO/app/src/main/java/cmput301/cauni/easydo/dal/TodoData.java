package cmput301.cauni.easydo.dal;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cmput301.cauni.easydo.R;
import cmput301.cauni.easydo.bll.TodoItem;
import cmput301.cauni.easydo.view.enums.TodoFiles;
import cmput301.cauni.easydo.view.enums.TodoOption;

public class TodoData
{
    private static class TodoDataSingleton
    {
        private static List<TodoItem> Active = null;
        private static List<TodoItem> Archived = null;
        //TODO: implement lock system

        public static List<TodoItem> getActive(Context c)
        {
            if (Active == null)
            {
                Active = loadTodos(c);
            }
            return Active;
        }
        public static List<TodoItem> getArchived(Context c)
        {
            if (Archived == null)
                Archived = loadArchivedTodos(c);
            return Archived;
        }
        public static void reset()
        {
            Active = null;
            Archived = null;
        }
    }

    //Update seed count and return it
    public final static int getTodoIdSeed(Context c)
    {
        SharedPreferences sharedPref = c.getSharedPreferences(
            c.getString(R.string.todo_seed_pref_key), Context.MODE_PRIVATE);
        int newSeed = sharedPref.getInt(c.getString(R.string.todo_seed_pref_key), 0) + 1;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(c.getString(R.string.todo_seed_pref_key), newSeed);
        editor.commit();

        return newSeed;
    }

    public final static void saveUserText(Context c, String text)
    {
        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getString(R.string.todo_user_text_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(c.getString(R.string.todo_user_text_key), text);
        editor.commit();
    }

    public final static String recoverUserText(Context c)
    {
        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getString(R.string.todo_user_text_key), Context.MODE_PRIVATE);
        return sharedPref.getString(c.getString(R.string.todo_user_text_key), "");
    }

    public final static void SaveTodos(Context c, List<TodoItem> obj)
    {
        Gson gson = new Gson();
        DataParser.SaveJson(c, TodoFiles.TODO, gson.toJson(Sorter(obj)));
        TodoDataSingleton.reset();
    }

    public final static void SaveArchivedTodos(Context c, List<TodoItem> obj)
    {
        Gson gson = new Gson();
        DataParser.SaveJson(c, TodoFiles.ARCHIVEDTODO, gson.toJson(Sorter(obj)));
        TodoDataSingleton.reset();
    }

    private final static List<TodoItem> loadTodos(Context c)
    {
        String aux = DataParser.LoadJson(c, TodoFiles.TODO);
        if (TextUtils.isEmpty(aux))
        {
            return new ArrayList<TodoItem>();
        }
        else
        {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<TodoItem>>(){}.getType();
            return Sorter((List<TodoItem>) gson.fromJson(aux, listType));
        }
    }

    public final static List<TodoItem> LoadTodos(Context c)
    {
        return TodoDataSingleton.getActive(c);
    }

    private final static List<TodoItem> loadArchivedTodos(Context c)
    {
        String aux = DataParser.LoadJson(c, TodoFiles.ARCHIVEDTODO);
        if (TextUtils.isEmpty(aux))
        {
            return new ArrayList<TodoItem>();
        }
        else
        {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<TodoItem>>() {
            }.getType();
            return Sorter((List<TodoItem>) gson.fromJson(aux, listType));
        }
    }

    public final static List<TodoItem> LoadArchivedTodos(Context c)
    {
        return TodoDataSingleton.getArchived(c);
    }

    protected final static List<TodoItem> Sorter(List<TodoItem> lst)
    {
        //First sort the list by date of creation
        Collections.sort(lst, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem obj1, TodoItem obj2) {
                return obj2.getDateCreated().compareTo(obj1.getDateCreated());
            }
        });
        return lst;
    }

    public final static List<TodoItem> LoadAllTodos(Context c)
    {
        List<TodoItem> aux = TodoDataSingleton.getActive(c);
        aux.addAll(TodoDataSingleton.getArchived(c));
        TodoDataSingleton.reset();
        return aux;
    }

    public final static int HandleArchive(Context c, List<Integer> lst, boolean archive)
    {
        try
        {
            List<TodoItem> objListSource = archive ? LoadTodos(c) : LoadArchivedTodos(c);
            List<TodoItem> objListDestination = archive ? LoadArchivedTodos(c) : LoadTodos(c);
            for (Iterator<TodoItem> i = objListSource.iterator(); i.hasNext();)
            {
                TodoItem obj = i.next();
                if (lst.contains(obj.getId())) {
                    obj.Archived = archive;
                    obj.setSelected(false);
                    objListDestination.add(obj);
                    i.remove();
                }
            }
            SaveTodos(c, archive ? objListSource : objListDestination);
            SaveArchivedTodos(c, archive ? objListDestination : objListSource);
        }
        catch (Exception e)
        {
            return 0;
        }
        return 1;
    }

    public final static int HandleCompleted(Context c, List<Integer> lst, boolean completed)
    {
        try
        {
            List<TodoItem> objList = LoadTodos(c);
            int lstSize = objList.size();
            for (Integer i : lst)
            {
                (objList.get(i)).Completed = completed;
            }
            SaveTodos(c, objList);
        }
        catch (Exception e)
        {
            return 0;
        }
        return 1;
    }

    public final static int HandleDelete(Context c, TodoOption opt, List<Integer> lst)
    {
        try
        {
            List<TodoItem> objList = opt == TodoOption.ACTIVE ? LoadTodos(c) : LoadArchivedTodos(c);
            int lstSize = objList.size();

            for (Iterator<TodoItem> i = objList.iterator(); i.hasNext();)
            {
                TodoItem obj = i.next();
                if (lst.contains(obj.getId())) {
                    i.remove();
                }
            }

            if (opt == TodoOption.ACTIVE)
                SaveTodos(c,objList);
            else
                SaveArchivedTodos(c,objList);
            return 1;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public final static List<TodoItem> getTodosFromList(Context c, List<Integer> lst, TodoOption opt)
    {
        List<TodoItem> objList = opt == TodoOption.ACTIVE ? LoadTodos(c) : LoadArchivedTodos(c);
        for (Iterator<TodoItem> i = objList.iterator(); i.hasNext();)
        {
            TodoItem obj = i.next();
            if (!lst.contains(obj.getId()))
            {
                i.remove();
            }
        }
        TodoDataSingleton.reset();
        return objList;
    }

    public final static HashMap<String, Integer> getSummary(Context c)
    {
        List<TodoItem> lstActive = LoadTodos(c);
        List<TodoItem> lstArchived = LoadArchivedTodos(c);
        HashMap<String, Integer> aux = new HashMap<String, Integer>();
        int lstActiveSize = lstActive.size();
        int lstArchivedSize = lstArchived.size();
        int checkedActives = 0;
        int uncheckedActives = 0;
        int checkedArchiveds = 0;
        int uncheckedArchiveds = 0;

        for (int i = 0; i < lstActiveSize; i++)
        {
            TodoItem obj = lstActive.get(i);
            if(obj.isCompleted())
                checkedActives++;
            else
                uncheckedActives++;
        }
        for (int i = 0; i < lstArchivedSize; i++)
        {
            TodoItem obj = lstArchived.get(i);
            if(obj.isCompleted())
                checkedArchiveds++;
            else
                uncheckedArchiveds++;
        }
        aux.put("summary_total_all", (lstActiveSize + lstArchivedSize));
        aux.put("summary_total", (lstActiveSize));
        aux.put("summary_total_checked", (checkedActives));
        aux.put("summary_total_unchecked", (uncheckedActives));
        aux.put("summary_total_archived", (lstArchivedSize));
        aux.put("summary_total_archived_checked", (checkedArchiveds));
        aux.put("summary_total_archived_unchecked", (uncheckedArchiveds));

        return aux;
    }
}