package com.example.socialreminder;

import android.util.Pair;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGenerator
{
    private static Double generateNumber()
    {
        Random rand = new Random();

        BigDecimal bd = new BigDecimal(rand.nextDouble() * 100).setScale(4, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static List<String> selectFriend(int iterations)
    {
        List<Pair<String, Pair<Double, Double>>> friends = new ArrayList<>();

        for(int i = 0; i < MainActivity.friends.size(); i++)
        {
            Friend f = MainActivity.friends.get(i);
            String name = f.name;
            boolean match = false;

            for(int j = 0; j < MainActivity.selectedFriends.size(); j++)
            {
                Friend f2 = MainActivity.selectedFriends.get(j);
                String name2 = f2.name;

                if(name.compareTo(name2) == 0)
                {
                    match = true;
                    break;
                }
            }

            if(!match)
            {
                Double upperBounds = f.getUpperBounds();
                Double lowerBounds = f.getLowerBounds();

                friends.add(new Pair<>(name, new Pair<>(lowerBounds, upperBounds)));
            }
        }

        List<String> temp = new ArrayList<>();
        //Returning empty lists and incomplete lists due to the friend at the index being removed
        //and the next friend being missed due to it taking the last friend's index while j keeps counting
        for(int i = 0; i < iterations; i++)
        {
            double rGen = generateNumber();
            for (int j = 0; j < friends.size(); j++)
            {
                Pair<String, Pair<Double, Double>> f1 = friends.get(j);

                if(f1.second.second > rGen)
                {
                    String name = f1.first;
                    temp.add(name);
                    friends = removeFriend(friends, j);

                    if(i == iterations - 1)
                        return temp;

                    break;
                }
            }
        }

        return null;
    }

    //Fix rounding error with bigdecimal
    private static List<Pair<String, Pair<Double, Double>>> removeFriend(List<Pair<String, Pair<Double, Double>>> friends, int friendToRemove)
    {
        BigDecimal bdm1 = new BigDecimal(friends.get(friendToRemove).second.second.toString()),
                bdm2 = new BigDecimal(friends.get(friendToRemove).second.first.toString());
        BigDecimal modifier = bdm1.subtract(bdm2);

        friends.remove(friendToRemove);

        if(friends.size() == 0) return friends;

        modifier = modifier.divide(new BigDecimal(String.valueOf(friends.size())), 5, RoundingMode.HALF_UP);

        for(int i = 0; i < friends.size(); i++)
        {
            double neighborValue = i == 0 ? 0 : friends.get(i - 1).second.second;

            BigDecimal bdw1 = new BigDecimal(friends.get(i).second.second.toString()),
                    bdw2 = new BigDecimal(friends.get(i).second.first.toString());
            BigDecimal weight = bdw1.subtract(bdw2);

            BigDecimal lowerBound, upperBound;
            lowerBound = new BigDecimal(neighborValue + "");
            lowerBound = lowerBound.setScale(4, RoundingMode.HALF_UP);
            upperBound = lowerBound.add(weight);
            upperBound = upperBound.add(modifier);
            upperBound = upperBound.setScale(4, RoundingMode.HALF_UP);

            friends.set(i, new Pair<>(friends.get(i).first, new Pair<>(lowerBound.doubleValue() , upperBound.doubleValue())));
        }
        return friends;
    }
}
