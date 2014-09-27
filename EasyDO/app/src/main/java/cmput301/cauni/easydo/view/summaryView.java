package cmput301.cauni.easydo.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;

import cmput301.cauni.easydo.R;
import cmput301.cauni.easydo.bll.TodoItem;

public class summaryView extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_view_fragment);
        HashMap<String,Integer> aux = TodoItem.getSummary(getBaseContext());

        ((TextView)findViewById(R.id.summary_total_all)).setText(aux.get("summary_total_all").toString());
        ((TextView)findViewById(R.id.summary_total)).setText(aux.get("summary_total").toString());
        ((TextView)findViewById(R.id.summary_total_checked)).setText(aux.get("summary_total_checked").toString());
        ((TextView)findViewById(R.id.summary_total_unchecked)).setText(aux.get("summary_total_unchecked").toString());
        ((TextView)findViewById(R.id.summary_total_archived)).setText(aux.get("summary_total_archived").toString());
        ((TextView)findViewById(R.id.summary_total_archived_checked)).setText(aux.get("summary_total_archived_checked").toString());
        ((TextView)findViewById(R.id.summary_total_archived_unchecked)).setText(aux.get("summary_total_archived_unchecked").toString());
    }
}
