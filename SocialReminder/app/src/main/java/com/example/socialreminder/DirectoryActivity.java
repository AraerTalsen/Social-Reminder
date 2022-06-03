package com.example.socialreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class DirectoryActivity extends AppCompatActivity {

    private LinearLayout directory;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(b ->
        {
            startActivity(new Intent(this, MainActivity.class));
        });

        Button resetAllMod = findViewById(R.id.resetAllMod);
        resetAllMod.setOnClickListener(v ->
        {
            MainManager.resetAllModifiers();
        });

        ImageButton printAllFriends = findViewById(R.id.printAllFriends);
        printAllFriends.setOnClickListener(v ->
        {
            for( Friend f : MainActivity.friends)
                f.printFriend();
        });

        directory = findViewById(R.id.directory);

        populateDirectory();
    }

    private void populateDirectory()
    {
        for(int i = 0; i < MainActivity.friends.size(); i++)
            directory.addView(new FriendDirItem(DirectoryActivity.this, MainActivity.friends.get(i)).getItem());
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        MainActivity.wrf.friendsToJSON();
    }
}