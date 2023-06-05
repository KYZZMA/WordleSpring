package com.wordle.game.dameDao;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


@Component
public class GameDao {

    static Scanner one = new Scanner(System.in);


    ArrayList<String> guesslist = new ArrayList<>();
    ArrayList<String> list = scannerWord(guesslist);

    String guess = null;
    String puzzleWord = randomPuzzleString(list, guess);
    public String check(String model) {
        /*
        В этом методе реализуется формирование словаря и передача его
        в следующий метод, а также передача рандомного слова, которое
        будет отгадываться.
         */

        return running(puzzleWord, list, model);
    }



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
                        result[i] = String.valueOf( model.charAt(i) );
                        resultMask[i] = "G";
                        continue;
                    } else if (puzzleWord.indexOf(model.charAt(i)) != -1) {
                        result[i] = "(" + model.charAt(i) + ")";
                        resultMask[i] = "Y";
                        flag = false;
                    } else {
                        result[i] = "-";
                        resultMask[i] = "X";
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
                if (newMask.equals("GGGGG")){
                    return newWord +" вы выиграли";
                }else
                    return newWord + " | " + newMask + " | " + sercheChar(list,model,newMask);


            } else {
                // если слово не было в словаре, то сообщаем об этом пользователю и повторяем метод
                return "такого слова нет";


            }

        }
        return newWord;
    }

    public static String sercheChar(ArrayList<String> list, String supposeWord, String newMask) {

        //Набор выражений, которые содержат все буквы, отфильтрованные по условию
        List<String> regexList = initializeRegex();

        // Символы которые содержат зеленый, или желтый цвет
        List<Character> detectedLetters = new ArrayList<>();
        //Метод который отфильтровывает выражения
        updateRegex(supposeWord, newMask, detectedLetters, regexList);

                //Метод который отфильтровывает все подходящие слова
        return  printTopMatches(detectedLetters, regexList, list);
    }

    private static String printTopMatches(List<Character> detectedLetters, List<String> regexList, List<String> wordList) {
        String regex = regexList.stream().collect(Collectors.joining());
        int printCount = 0;
        ArrayList<String> arrw = new ArrayList<>();
        for (int i = 0; i < wordList.size(); i++) {
            String currentWord = wordList.get(i);
            // Проверяем что слово содержит все обнаруженные символы и корректно
            if (currentWord.matches(regex) && allDetectedLettersPresent(currentWord, detectedLetters)) {
               arrw.add(currentWord);
                if (++printCount == 10) {
                    break;
                }

            }

        }
        String arrS = "";
        for (String x : arrw){
            arrS += x+" ";
        }

        return arrS;

    }

    private static boolean allDetectedLettersPresent(String currentWord, List<Character> detectedLetters) {
        //проверяем слово на наличие в нем обнаруженных символов
        for (int i = 0; i < detectedLetters.size(); i++) {
            if (!currentWord.contains(detectedLetters.get(i).toString())) {
                return false;
            }
        }
        return true;
    }

    //обновление
    private static void updateRegex(String guess, String feedback, List<Character> detectedLetters, List<String> regexList) {
        for (int i = 0; i < 5; i++) {

            char currentChar = guess.charAt(i);
            char feedbackForCurrentChar = feedback.charAt(i);

            if (isGrayed(feedbackForCurrentChar)) {
                /*
                 Если наше предполагаемое слово содержит символы серого цвета, то есть те которые не входят в состав
                 загаданного слова, мы должны удалить эти символы, но, может возникнуть ситуация, когда данный символ маркируется
                 желтым или зеленым цветом, то есть присутствует, но может быть не на своем месте, в таком случае
                 слово необходимо оставить, удалить символ только для текущей позиции и продолжать делать проверки.
                 В других случах, необходимо удалить все позиции данных символов.

                 */
                //удаляем символ если он повторяется несколько раз и позиция где он точно должен быть неизвестна
                if (detectedLetters.contains(currentChar)) {
                    String currRegex = regexList.get(i);
                    String updatedRegex = currRegex.replace(currentChar, '\0');
                    regexList.set(i, updatedRegex);
                } else {
                    // удаляем текущий символ из всех позиций в выражениях
                    for (int regexIndex = 0; regexIndex < 5; regexIndex++) {
                        String currRegex = regexList.get(regexIndex);
                        String updatedRegex = currRegex.replace(currentChar, '\0');
                        regexList.set(regexIndex, updatedRegex);
                    }
                }
            } else if (isYellow(feedbackForCurrentChar)) {
                //В случае когда у нас символ не на своем месте, то в вырвжении убираем сам символ именно на позии где он несовпал
                String currRegex = regexList.get(i);
                String updatedRegex = currRegex.replace(currentChar, '\0');
                regexList.set(i, updatedRegex);

                detectedLetters.add(guess.charAt(i));
            } else if (isGreen(feedbackForCurrentChar)) {
                // Здесь вместо выражения с символами сразу указываем символ который на своей позиции
                regexList.set(i, String.valueOf(currentChar));
                detectedLetters.add(guess.charAt(i));
            }
        }
    }

    private static List<String> initializeRegex() {
        List<String> regexList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            regexList.add("[абвгдеёжзийклмнопрстуфхцчшщъыьэюя]");
        }
        return regexList;
    }

    private static boolean isGrayed(char c) {
        return c == 'X' || c == 'x';
    }

    private static boolean isGreen(char c) {
        return c == 'G' || c == 'g';
    }

    private static boolean isYellow(char c) {
        return c == 'Y' || c == 'y';
    }
}
