package com.example.socialreminder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public class FriendDirItem
{
    private CardView item;
    private Friend friend;

    public FriendDirItem(Context c, Friend f)
    {
        friend = f;
        assembleDirItem(c, f);
    }

    private void assembleDirItem(Context c, Friend f)
    {
        CardView cv = new CardView(c);
        ViewGroup.LayoutParams cvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        cv.setCardBackgroundColor(Color.parseColor("#fcf8c5"));
        cv.setRadius(16);

        TextView t = new TextView(c);
        ViewGroup.LayoutParams tParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 100);
        t.setLayoutParams(tParams);
        t.setText(f.name);
        t.setTextColor(Color.BLACK);
        t.setTextSize(20);
        t.setTranslationX(50);

        Button b1 = new Button(c);
        ViewGroup.LayoutParams b1Params = new ViewGroup.LayoutParams(100, 100);
        b1.setLayoutParams(b1Params);
        b1.setText("R");
        b1.setTextColor(Color.BLACK);
        b1.setTranslationX(850);
        b1.setOnClickListener(v ->
        {
            friend.reset();
            System.out.println(friend.name + " Reset");
        });

        Button b2 = new Button(c);
        ViewGroup.LayoutParams b2Params = new ViewGroup.LayoutParams(100, 100);
        b2.setLayoutParams(b2Params);
        b2.setText("D");
        b2.setTextColor(Color.BLACK);
        b2.setTranslationX(950);
        b2.setOnClickListener(v ->
        {
            System.out.println(friend.name + " Deleted");
            friend.delete();
            item.setVisibility(View.GONE);
        });

        cv.addView(t);
        cv.addView(b1);
        cv.addView(b2);

        item = cv;
    }

    public CardView getItem() { return item; }
}
