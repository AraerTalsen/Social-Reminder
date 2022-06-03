package com.example.socialreminder;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;

public class WriteReadFile extends AppCompatActivity
{
    private Date currentDate = Calendar.getInstance().getTime(), saveDate;
    public static List<String> friendsJSON = new ArrayList<>();
    private String dirName = "friendsList";
    public static final String friendFile = "friendsList.json";
    public static final String settingsFile = "settings.json";
    public static File path;

    public void updateJSONFile(String jsonString, String fileName)
    {
        try
        {
            File friendsList = new File(path, fileName);
            FileWriter writer = new FileWriter(friendsList);
            writer.append(jsonString);
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            System.out.println("Couldn't write file");
            e.printStackTrace();
        }

        friendsJSON.clear();
    }

    public void loadJSONFile(String fileName)
    {
        System.out.println("Attempting to load items from file");
        File f = new File(path + "/" + fileName);

        try
        {
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String currentLine;

            while((currentLine = br.readLine()) != null)
            {
                sb.append(currentLine).append("\n");
            }

            fis.close();
            if(fileName.compareTo(friendFile) == 0) jsonToFriend(sb.toString());
            else populateSettings(sb.toString());

        }
        catch (FileNotFoundException e)
        {
            System.out.println("Couldn't find file");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void friendsToJSON()
    {
        System.out.println("Writing friends to file");
        for(int i = 0; i < MainActivity.friends.size(); i++)
        {
            MainActivity.friends.get(i).friendToJSON();
        }

        String jsonString = "{\n\t\"friends\":\n\t[\n";

        for(int i = 0; i < friendsJSON.size(); i++)
        {
            String ext = i < friendsJSON.size() - 1 ? ",\n" : "\n";
            jsonString += (friendsJSON.get(i) + ext);
        }

        jsonString += "\t]\n}";

        updateJSONFile(jsonString, friendFile);
    }

    private void populateSettings(String s)
    {
        JSONObject obj = null;
        try
        {
            obj = new JSONObject(s);
            if(obj.getString("date").compareTo("null") != 0)
                saveDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(obj.getString("date"));
            MainManager.alarmSet = obj.getBoolean("alarm");

            if(MainActivity.friends.size() > 0)
            {
                JSONArray arr = obj.getJSONArray("friends");
                String[] names = new String[arr.length()];
                for(int i = 0; i < arr.length(); i++)
                {
                    JSONObject friend = arr.getJSONObject(i);
                    names[i] = friend.getString("name");
                }

                MainActivity.selectedFriends = findFriends(names);
            }
        }
        catch (JSONException | ParseException e)
        {
            e.printStackTrace();
        }
    }

    public void settingsToJSON()
    {
        String jsonString = "{\n\t";
        jsonString += "\"date\": \"";
        jsonString += saveDate + "\",\n\t";
        jsonString += "\"alarm\": \"" + MainManager.alarmSet + "\",\n\t";

        for(int i = 0; i < MainActivity.selectedFriends.size(); i++)
        {
            MainActivity.selectedFriends.get(i).friendToJSON();
        }

        jsonString += "\"friends\":\n\t[\n";

        for(int i = 0; i < friendsJSON.size(); i++)
        {
            String ext = i < friendsJSON.size() - 1 ? ",\n" : "\n";
            jsonString += (friendsJSON.get(i) + ext);
        }

        jsonString += "\t]\n}";


        updateJSONFile(jsonString, settingsFile);
    }

    private List<Friend> jsonToFriend(String s)
    {
        JSONObject obj = null;
        try
        {
            obj = new JSONObject(s);
            JSONArray arr = obj.getJSONArray("friends");

            for(int i = 0; i < arr.length(); i++)
            {
                JSONObject friend = arr.getJSONObject(i);
                Friend f = new Friend(
                        friend.getString("name"), friend.getDouble("modifier"), MainActivity.friends);
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void setSaveDate(Date d) { saveDate = d; }

    private List<Friend> findFriends(String[] names)
    {
        List<Friend> f = new ArrayList<>();
        for(String n : names)
        {
            for(int i = 0; i < MainActivity.friends.size(); i++)
            {
                if(MainActivity.friends.get(i).name.compareTo(n) == 0)
                {
                    f.add(MainActivity.friends.get(i));
                    break;
                }
                else if(i == MainActivity.friends.size() - 1)
                    System.out.println("Could not find " + n + " in friends list to add to selected friends.");
            }
        }
        return f;
    }

    public Date getSaveDate() { return saveDate; }
}
