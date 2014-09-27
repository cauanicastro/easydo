package cmput301.cauni.easydo.dal;

import android.content.Context;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import cmput301.cauni.easydo.view.enums.TodoFiles;

/*
* Class responsible for handling the xml data and storing it in the internal memory,
* and retrieving the xml from the xml file in the internal memory
* */
public class DataParser
{
    public final static void SaveJson(Context c, TodoFiles filename, String json)
    {
        FileOutputStream outputStream;
        try
        {
            outputStream = c.openFileOutput(filename.getArchiveName(), Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public final static String LoadJson(Context c, TodoFiles filename)
    {
        StringBuffer sBuffer = new StringBuffer("");
        try
        {
            FileInputStream inputStream = null;
            try
            {
                inputStream = c.openFileInput(filename.getArchiveName());
            }
            catch (IOException e)
            {
                FileOutputStream outputStream;
                outputStream = c.openFileOutput(filename.getArchiveName(), Context.MODE_PRIVATE);
                outputStream.flush();
                outputStream.close();

                inputStream = c.openFileInput(filename.getArchiveName());
            }
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputReader);

            String readString = bufferReader.readLine();
            while (readString != null)
            {
                sBuffer.append(readString);
                readString = bufferReader.readLine();
            }
            inputReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return sBuffer.toString();
    }
}
