package com.example.socialreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.acl.NotOwnerException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static  List<Friend> selectedFriends = new ArrayList<>();
    public static List<Friend> friends = new ArrayList<>();
    public static ArrayAdapter<Friend> adapter;
    public static final WriteReadFile wrf = new WriteReadFile();
    public static int talkTotal = 0, selectedTotal = 3;
    private Calendar calendar = Calendar.getInstance();
    private AlarmManager alarmManager, alarmManager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        WriteReadFile.path = MainActivity.this.getFilesDir();


        if(!MainManager.started)
        {
            MainManager.started = true;
            wrf.loadJSONFile(wrf.friendFile);
            wrf.loadJSONFile(wrf.settingsFile);
        }

        Button addFriend = findViewById(R.id.addFriend);
        CardView cv = findViewById(R.id.newFriendWindow);

        LinearLayout selectedFriendsView = findViewById(R.id.selectedFriendsScroll);

        boolean weekGoneBy =
                wrf.getSaveDate() != null && wrf.getSaveDate().getTime() + 1000 * 60 * 60 * 24 * 7 <= Calendar.getInstance().getTimeInMillis();
        boolean friendsInList = friends.size() > 0;
        boolean selectedNotFull = selectedFriends.size() < selectedTotal;
        boolean moreAvailable = selectedFriends.size() < friends.size();

        if(friendsInList && (weekGoneBy || (selectedNotFull && moreAvailable)))
        {
            if(weekGoneBy)
            {
                Calendar ca = Calendar.getInstance();
                wrf.setSaveDate(ca.getTime());
                selectedFriends.clear();
                applyModifierUpdates();
            }


            int num = friends.size() < selectedTotal ? friends.size() : selectedTotal;
            num -= selectedFriends.size();

            List<String> temp = RandomGenerator.selectFriend(num);

            if(temp != null)
            {
                selectedFriends.addAll(findFriends(temp));

                for(int i = 0; i < selectedFriends.size(); i++)
                {
                    selectedFriendsView.addView(new FriendCard(
                            selectedFriends.get(i), MainActivity.this).getFriendCard());
                }
            }
        }
        else if(selectedFriends.size() > 0)
        {
            for(int i = 0; i < selectedFriends.size(); i++)
            {
                selectedFriendsView.addView(new FriendCard(selectedFriends.get(i), MainActivity.this).getFriendCard());
            }
        }

        adapter = new ArrayAdapter<Friend>(this, android.R.layout.simple_dropdown_item_1line, friends);
        EditText writeName = findViewById(R.id.name);
        writeName.setHint("Name");

        addFriend.setOnClickListener(v ->
        {
            cv.setVisibility(View.VISIBLE);

            writeName.setOnKeyListener((view, i, keyEvent) ->
            {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER))
                {
                    try
                    {
                        String name = writeName.getText().toString();
                        new Friend(name, 0, friends);
                        writeName.setText("");
                        cv.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e)
                    {
                        System.out.println("Failed to read name of new friend");
                        e.printStackTrace();
                    }
                }
                return false;
            });
        });

        Button exitNewFriend = findViewById(R.id.exitNewFriend);
        exitNewFriend.setOnClickListener(v ->
        {
            cv.setVisibility(View.INVISIBLE);
        });

        AutoCompleteTextView searchFriend = findViewById(R.id.autoCompleteTextView);
        searchFriend.setHint("Search Different Friend");
        searchFriend.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Friend f = (Friend)parent.getItemAtPosition(position);
                f.setTalkedTo(true);
            }
        });
        searchFriend.setAdapter(adapter);

        Button friendsDir = findViewById(R.id.friendsDir);

        friendsDir.setOnClickListener(v ->
        {
            startActivity(new Intent(this, DirectoryActivity.class));
        });

        ImageButton restartAlarm = findViewById(R.id.restartAlarm);
        restartAlarm.setOnClickListener(v ->
        {
            setAlarmNewSelection();
            setAlarmReminder();
        });
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        wrf.friendsToJSON();
        if(friends.size() > 0 && !MainManager.alarmSet)
        {
            setAlarmNewSelection();
            setAlarmReminder();
            wrf.setSaveDate(calendar.getTime());
        }
        else if(friends.size() == 0 && MainManager.alarmSet)
            cancelAlarm();
        wrf.settingsToJSON();
    }

    public static void applyModifierUpdates()
    {
        if(talkTotal != 0 && talkTotal != friends.size())
        {
            double addWeight = talkTotal * Friend.weight.doubleValue() * 0.25 / (friends.size() - (double)talkTotal);

            for(int i = 0; i < friends.size(); i++)
            {
                double mod = friends.get(i).getTalkedTo() ? Friend.weight.doubleValue() * -0.25 : addWeight;
                friends.get(i).incrementModifier(mod);
                friends.get(i).setTalkedTo(false);
                friends.get(i).printFriend();
            }
        }
        talkTotal = 0;
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Main";
            String description = "Main notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("mChannel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarmNewSelection()
    {
        System.out.println("Alarm set");
        MainManager.alarmSet = true;

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 6);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);


        if(Calendar.DAY_OF_WEEK >= Calendar.MONDAY)
        {
            int waitNDays = 7 - Calendar.DAY_OF_WEEK;
            ca.set(Calendar.DATE, Calendar.DATE + waitNDays);
        }

        Intent intent = new Intent(this, CallCreateNotification.class);
        intent.putExtra(CallCreateNotification.INTENTID, 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(),
                1000 * 60 * 60 * 24 * 7, pendingIntent);
    }

    private void setAlarmReminder()
    {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 19);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        if(Calendar.DAY_OF_WEEK >= Calendar.THURSDAY)
        {
            int waitNDays = 7 - Calendar.DAY_OF_WEEK;
            ca.set(Calendar.DATE, Calendar.DATE + waitNDays);
        }

        Intent intent = new Intent(this, CallCreateNotification.class);
        intent.putExtra(CallCreateNotification.INTENTID, 1);

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, 1, intent, 0);
        alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(),
                1000 * 60 * 60 * 24 * 7, pendingIntent);
    }

    private void cancelAlarm()
    {
        System.out.println("Canceling alarm");
        MainManager.alarmSet = false;
        Intent intent = new Intent(this, CallCreateNotification.class);
        intent.putExtra(CallCreateNotification.INTENTID, 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, 0, intent, 0);

        Intent intent2 = new Intent(this, CallCreateNotification.class);
        intent2.putExtra(CallCreateNotification.INTENTID, 1);

        PendingIntent pendingIntent2 = PendingIntent.getBroadcast
                (this, 1, intent2, 0);

        if(alarmManager == null)
        {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        alarmManager2.cancel(pendingIntent2);

        wrf.setSaveDate(null);
    }

    private Friend findFriend(String name)
    {
        Friend f = null;
        for(int i = 0; i < friends.size(); i++)
        {
            if(friends.get(i).name.compareTo(name) == 0)
            {
                f = friends.get(i);
                break;
            }
        }
        return f;
    }

    private List<Friend> findFriends(List<String> names)
    {
        List<Friend> f = new ArrayList<>();
        for(int i = 0; i < friends.size(); i++)
        {
            for(int j = 0; j < names.size(); j++)
            {
                if(friends.get(i).name.compareTo(names.get(j)) == 0)
                {
                    f.add(friends.get(i));
                    break;
                }
            }
        }
        return f;
    }
}