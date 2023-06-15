package com.wordle.game.settingsGame;

import java.util.ArrayList;

public class ProgramLaunch {
    public static int count = 1;
    public static String running(String puzzleWord, ArrayList<String> list, String model) {
        //запускаем цикл с условием для выхода из него
        String newWord = null;
        String newMask = null;
        boolean flag = false;
        while (!flag) {
            flag = true;

            //делаем проверку на корректность введенного слова
            if (list.contains(model)) {

                //при нахождении предполагаемого слова, сравниваем его символы с загаданным словом

                String[] result = new String[puzzleWord.length()];
                String[] resultMask = new String[puzzleWord.length()];

                for (int i = 0; i < model.length(); i++) {
                    if (puzzleWord.charAt(i) == model.charAt(i)) {
                        result[i] = String.valueOf(model.charAt(i));
                        resultMask[i] = "G";
                        continue;
                    } else if (puzzleWord.indexOf(model.charAt(i)) != -1) {
                        result[i] = "(" + model.charAt(i) + ")";
                        resultMask[i] = "Y";
                        flag = false;
                    } else {
                        result[i] = "-";
                        resultMask[i] = "N";
                        flag = false;
                    }
                }

                //делаем проверку в словаре со словами, которые подходят по найденным символам
                StringBuilder sbR = new StringBuilder();
                for (String ch : result) {
                    sbR.append(ch);
                }
                newWord = sbR.toString();

                StringBuilder sbM = new StringBuilder();
                for (String ch : resultMask) {
                    sbM.append(ch);
                }
                newMask = sbM.toString();
                // проверяем маску на сходство с загаданным словом, если маска не идентична, то продолжаем проверку и поиск
                if (newMask.equals("GGGGG")) {
                    return newWord + " вы выиграли " + count;
                } else {
                    return newWord + " | " + newMask + " | " + count++ + " | "+ CheckingGuess.sortWeight(CheckingGuess.sercheChar(list, model, newMask));
                }

            } else {
                // если слово не было в словаре, то сообщаем об этом пользователю и повторяем метод
                return "такого слова нет";


            }

        }
        return newWord;
    }
}
