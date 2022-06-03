package com.example.socialreminder;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Friend
{
    public static BigDecimal weight;
    public String name;
    private BigDecimal lowerBounds, upperBounds;
    private double modifier;
    public static Friend first, last;
    private boolean talkedTo = false;
    private int index;
    private List<Friend> list;

    public Friend(String n, double modifier, List<Friend> l)
    {
        name = n;
        this.modifier = modifier;
        list = l;
        list.add(this);
        index = list.size() - 1;

        if(index == 0) first = this;
        else last = this;

        setWeight();
        updateAllBounds();
        //printFriend();
    }

    public void setWeight()
    {
        if(list.size() > 0)
            weight = new BigDecimal(100 / (double)list.size()).setScale(5, RoundingMode.HALF_UP);
        else weight = new BigDecimal(100);
    }

    public void setBounds(int index)
    {
        double neighborVal = index != 0 ? list.get(index - 1).getUpperBounds() : 0;

        lowerBounds = new BigDecimal(neighborVal + "").setScale(4, RoundingMode.HALF_UP);
        upperBounds = lowerBounds.add(weight).setScale(4, RoundingMode.HALF_UP);
        upperBounds =
                upperBounds.add(new BigDecimal(modifier).setScale(5, RoundingMode.HALF_UP)).setScale(4, RoundingMode.HALF_UP);
    }

    public void updateAllBounds()
    {
        for(int i = 0; i < list.size(); i++)
        {
            list.get(i).setBounds(i);
        }

    }

    public void friendToJSON()
    {
        String n = "\"name\": \"" + name + "\"";
        String m = "\"modifier\": \"" + modifier + "\"";
        String i = "\"index\": \"" + index + "\"";

        String obj = "\t\t{\n\t\t\t" + n + ",\n\t\t\t" + m + ",\n\t\t\t" + i + "\n\t\t}";
        WriteReadFile.friendsJSON.add(obj);
    }

    public void printFriend()
    {
        System.out.println("Name: " + name + "\n"
            + "Modifier: " + modifier + "\n"
            + "Lower Bounds: " + lowerBounds + "\n"
            + "Upper Bounds: " + upperBounds + "\n"
            + "Index: " + index
        );
    }

    public void delete()
    {
        reset();
        list.remove(index);
        for(int i = index; i < list.size(); i++)
            list.get(i).setIndex(index + (i - index));

        setWeight();
        updateAllBounds();

        if(MainActivity.friends.size() == 0)
            MainActivity.selectedFriends.clear();

        for(int i = 0; i < MainActivity.selectedFriends.size(); i++)
        {
            Friend f = MainActivity.selectedFriends.get(i);
            if(f == this)
                MainActivity.selectedFriends.remove(i);
        }
    }

    public double getUpperBounds()
    {
        return upperBounds.doubleValue();
    }
    public double getLowerBounds()
    {
        return lowerBounds.doubleValue();
    }
    public boolean getTalkedTo() { return talkedTo; }

    public void reset()
    {
        if(talkedTo)
            MainActivity.talkTotal--;
        setTalkedTo(false);

        double addWeight = modifier / (MainActivity.friends.size() - 1);
        for(int i = 0; i < MainActivity.friends.size(); i++)
        {
            if(i != index)
            {
                MainActivity.friends.get(i).incrementModifier(addWeight);
            }
            else
            {
                modifier = 0;
                setBounds(index);
            }
        }
    }

    //Use only for resetting all modifiers (for now)
    public void setModifier(double modifier) { this.modifier = modifier; }

    public double getModifier() { return modifier; }

    public void setTalkedTo(boolean isTalkedTo)
    {
        if(isTalkedTo) MainActivity.talkTotal++;
        talkedTo = isTalkedTo;
    }

    public void incrementModifier(double modifier)
    {
        this.modifier += modifier;
        setBounds(index);
    }

    public void setIndex(int index) { this.index = index; }

    public int getIndex() { return index; }

    @Override
    public String toString()
    {
        return name;
    }
}
