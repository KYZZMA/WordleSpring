package com.wordle.game.settingsGame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DataLayer {

    public static ArrayList<String> scannerWord(ArrayList<String> list) {

        File path = new File("E:/vocabulary.txt");

        // пробуем считать словарь из файла в список
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String s;
            while ((s = br.readLine()) != null) {
                String[] split = s.split("\n");
                list.addAll(Arrays.asList(split));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

    public static String randomPuzzleString(ArrayList<String> list, String puzzleWord) {
        // выбираем рандомное слово мз списка, которое будет загадываться
        int random = (int) (Math.random() * list.size());
        puzzleWord = list.get(random);

        return puzzleWord;
    }
}
