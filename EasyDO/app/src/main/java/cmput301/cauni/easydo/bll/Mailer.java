package cmput301.cauni.easydo.bll;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import cmput301.cauni.easydo.dal.TodoData;
import cmput301.cauni.easydo.view.enums.TodoOption;

public class Mailer
{
    private static String ExtractEmails(List<TodoItem> objList)
    {
        String lf = System.getProperty("line.separator");
        StringBuilder sbCompleted = new StringBuilder().append("Completed TODOs:" + lf + lf);
        StringBuilder sbArchived = new StringBuilder().append("Archived TODOs:" + lf + lf);
        StringBuilder sbTodo = new StringBuilder().append("Active TODOs:" + lf + lf);
        List<TodoItem> auxL = objList;
        int counter = auxL.size();
        for (int i = 0; i < counter; i++)
        {
            TodoItem obj = auxL.get(i);
            String aux = obj.getTask() + ";" + lf;
            if(obj.isArchived())
            {
                sbArchived.append(aux);
            }
            else if (obj.isCompleted())
            {
                sbCompleted.append(aux);
            }
            else
            {
                sbTodo.append(aux);
            }
        }
        StringBuilder mailbody = new StringBuilder().append("EasyDO, Easy to do - TODOs export summary" + lf + lf + lf);
        mailbody.append(sbTodo.toString().endsWith(";"+lf) ? sbTodo.toString().substring(0, sbTodo.toString().length() - 2) + "." + lf + lf : "");
        mailbody.append(sbCompleted.toString().endsWith(";"+lf) ? sbCompleted.toString().substring(0, sbCompleted.toString().length() - 2) + "." + lf + lf : "");
        mailbody.append(sbArchived.toString().endsWith(";"+lf) ? sbArchived.toString().substring(0, sbArchived.toString().length() - 2) + "." + lf + lf : "");

        return (mailbody.toString() == ("EasyDO, Easy to do - TODOs export summary" + lf + lf + lf))
            ? "Hey!" + lf + "Check out EasyDO, an amazing app to manage your daily tasks!" + lf + lf + "EasyDO, easy to do."
            : mailbody.toString();
    }

    private final static Intent SendMail(String text)
    {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "EasyDO - TODO Summary");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        return emailIntent;
    }

    public static Intent EmailAll(Context c)
    {
        return SendMail(ExtractEmails(TodoData.LoadAllTodos(c)));
    }

    public static Intent EmailTodoList(Context c, List<Integer> lst, TodoOption opt)
    {
        return SendMail(ExtractEmails(TodoData.getTodosFromList(c,lst, opt)));
    }
}