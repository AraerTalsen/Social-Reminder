package com.example.socialreminder;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.jar.Attributes;

public class FriendCard
{
    private Friend friend;
    private CardView friendCard;

    public FriendCard(Friend friend, Context c)
    {
        this.friend = friend;

        if(friend != null)
            assembleCard(c);
    }

    private void assembleCard(Context c)
    {
        ViewGroup.LayoutParams cvParams = new ViewGroup.LayoutParams(200, 200);

        ViewGroup.LayoutParams bParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        CardView cv = new CardView(c);
        cv.setLayoutParams(cvParams);
        cv.setCardBackgroundColor(Color.parseColor("#fcf8c5"));
        cv.setRadius(16);

        Button b = new Button(c);
        b.setLayoutParams(bParams);
        b.setBackgroundColor(Color.parseColor("#fcf8c5"));

        float txtSize = 20;
        int size = friend.name.length();

        if(size > 10 && size <= 15) txtSize = 15;
        else if(size > 15) txtSize = 10;

        b.setTextSize(txtSize);
        b.setTextColor(Color.BLACK);
        b.setText(friend.name);

        b.setOnClickListener(v ->
        {
            friend.setTalkedTo(true);

            cv.setVisibility(View.GONE);
        });

        cv.addView(b);
        if(friend.getTalkedTo())
            cv.setVisibility(View.GONE);

        friendCard = cv;
    }

    public CardView getFriendCard() { return friendCard; }
}
