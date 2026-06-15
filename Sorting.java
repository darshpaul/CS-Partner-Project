package com.fitness;

import java.util.ArrayList;

public class Sorting {

    public static void bubbleSortByProgress(ArrayList<Goal> goals, double[] progress) {
        int n = progress.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {

                if (progress[j] < progress[j + 1]) {

                    double tempNumber = progress[j];
                    progress[j] = progress[j + 1];
                    progress[j + 1] = tempNumber;

                    Goal tempGoal = goals.get(j);
                    goals.set(j, goals.get(j + 1));
                    goals.set(j + 1, tempGoal);
                }
            }
        }
    }
}
