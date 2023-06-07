package com.wordle.game.dameDao;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


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
                    return newWord + " вы выиграли";
                } else
                    return newWord + " | " + newMask + " | " + sercheChar(list, model, newMask);


            } else {
                // если слово не было в словаре, то сообщаем об этом пользователю и повторяем метод
                return "такого слова нет";


            }

        }
        return newWord;
    }

    public static String sercheChar(List<String> list, String newWord, String newMask) {
        //реализуем проверку слов с выявленными символами
        String result = "";
        for (int x = 0; x < list.size(); x++) {
            StringBuilder chars = new StringBuilder();
            String wordGuess = list.get(x);
            for (int i = 0; i < newWord.length(); i++) {
                if (newWord.charAt(i) == list.get(x).charAt(i)) {
                    chars.append("G");
                } else if (newWord.indexOf(list.get(x).charAt(i)) != -1) {
                    chars.append("Y");
                } else {
                    chars.append("N");
                }
            }

            String newChars = String.valueOf(chars);
            if (newMask.contains("Y") && chars.toString().contains("G")) {
                String regO = "";
                if (newMask.contains("Y")) {
                    int regOI = newMask.indexOf("Y");
                    regO = String.valueOf(newWord.charAt(regOI));

                }
                String regC = "";

                if (chars.toString().contains("G")) {
                    int regCI = chars.indexOf("G");
                    regC = String.valueOf(list.get(x).charAt(regCI));
                }

                if (regO.equals(regC)) {
                    int regCi = chars.indexOf("G");
                    newChars = chars.toString().replace("G", "T");
                }
            }


            HashMap<String, String> mapa = new HashMap<>();
            greyChar(newChars, newMask, wordGuess, newWord, mapa);
            yellowChar(newMask, newWord, mapa);
            greenChar(newMask, newWord, mapa);


            int printCount = 0;
            ArrayList<String> arrw = new ArrayList<>();
            for (Map.Entry<String, String> entry1 : mapa.entrySet()) {
                String s = (String) entry1.getKey();
                String c = (String) entry1.getValue();
                arrw.add(c);


            }

//            for (int b = 0; b != 10;) {
//                result += arrw.get(b) + " ";
//                b++;
//            }

                for (String d : arrw) {
                    result += d + " ";
                }





        }
        return result;
    }

    public static HashMap<String, String> greyChar(String chars, String newMask, String wordGuess, String newWord, HashMap<String, String> filtrarray) {


        String[] arrReg = new String[]{" ", " ", " ", " ", " "};
        String[] oldMask = newMask.split("");
        if (newMask.contains("N")) {
            for (int i = 0; i < oldMask.length; i++) {
                if (oldMask[i].equals("N")) {
                    arrReg[i] = String.valueOf(newWord.charAt(i));
                }
            }
        }
        int count = 0;
        for (int i = 0; i < arrReg.length; i++) {
            if (!wordGuess.contains(arrReg[i])) {
                count++;
            }
        }
        if (count == arrReg.length) {
            filtrarray.put(chars, wordGuess);
        }


        return filtrarray;
    }

    public static HashMap<String, String> yellowChar(String newMask, String newWord, HashMap<String, String> filtrarray) {


        for (Map.Entry<String, String> entry : filtrarray.entrySet()) {
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (newMask.contains("Y")) {
                String[] oldMask = newMask.split("");
                String[] charsMask = k.split("");
                String[] guessWordArr = v.split("");
                int count = 0;

                ArrayList<String> countYO = new ArrayList<>();
                ArrayList<String> countYC = new ArrayList<>();


                String[] arrReg = new String[]{" ", " ", " ", " ", " "};
                if (newMask.contains("Y") && k.contains("Y") || k.contains("T")) {
                    for (int i = 0; i < oldMask.length; i++) {
                        if (charsMask[i].equals("Y") || charsMask[i].equals("T")) {
                            countYC.add(String.valueOf(v.charAt(i)));

                        }
                        if (oldMask[i].equals("Y")) {
                            countYO.add(String.valueOf(newWord.charAt(i)));
                            arrReg[i] = String.valueOf(newWord.charAt(i));
                        } else {
                            arrReg[i] = "-";
                        }
                    }
                }


                Collections.sort(countYO);
                Collections.sort(countYC);


                String resultO = "";
                for (String i : countYO) {
                    resultO += i;
                }
                String newResultO = resultO.replaceAll("(.)(?=.*\\1)", "");
                String resultC = "";
                for (String i : countYC) {
                    resultC += i;
                }
                String newResultC = resultC.replaceAll("(.)(?=.*\\1)", "");

                if (newResultO.equals(newResultC) && !resultC.equals("")) {


                    for (int i = 0; i < arrReg.length; i++) {

                        if (!arrReg[i].equals(guessWordArr[i])) {
                            count++;
                        }
                    }

                }

                if (count == 5) {
                    continue;
                } else {
                    filtrarray.remove(k);
                }

            } else {
                continue;
            }
        }
        return filtrarray;
    }

    public static HashMap<String, String> greenChar(String newMask, String newWord, HashMap<String, String> filtrarray) {

        for (Map.Entry<String, String> entry : filtrarray.entrySet()) {
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (newMask.contains("G")) {
                String[] arrMask = newMask.replace("N", "-").replace("Y", "-").split("");
                StringBuilder sv = new StringBuilder();
                for (String ch : arrMask) {
                    sv.append(ch);
                }
                String word3 = sv.toString();

                String[] arrMaskN1 = k.replace("N", "-").replace("Y", "-").replace("T", "-").split("");
                StringBuilder sr = new StringBuilder();
                for (String ch : arrMaskN1) {
                    sr.append(ch);
                }
                String word4 = sr.toString();

                if (word3.equals(word4)) {
                    continue;
                } else {
                    filtrarray.remove(k);
                }
            } else {
                continue;
            }

        }
        return filtrarray;
    }
}
