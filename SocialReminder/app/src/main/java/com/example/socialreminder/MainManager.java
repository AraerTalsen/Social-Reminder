package com.example.socialreminder;

public class MainManager
{
    public static boolean started = false;
    public static boolean alarmSet = false;

    public static void resetAllModifiers()
    {
        int i = 0;
        for(Friend f : MainActivity.friends)
        {
            f.setModifier(0);
            f.setTalkedTo(false);
            f.setBounds(i);
            i++;
        }
        MainActivity.talkTotal = 0;
    }
}
