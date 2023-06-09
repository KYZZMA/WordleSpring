package com.wordle.game.settingsGame;

import java.util.*;
import java.util.stream.Collectors;

public class CheckingGuess {
    public static ArrayList<String> sercheChar(List<String> list, String newWord, String newMask) {
        //реализуем создание масок для слов в списке, проверяя их на схождение маски предпологаемого слова которое вводили ранее
        ArrayList<String> arrw = new ArrayList<>();

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



            String[] oldMask = newMask.split("");
            String[] charMask = chars.toString().split("");

            String newChars = String.valueOf(chars);
            String oldChars = newMask;

            /*
             тут реализуем проверку символа, который может быть в предполагаемом слове дважды и иметь как
             маску G так и Y, если в слове находится такой символ и он не на своем месте, имеет маску Y
             и при этом находится на другой позиции под маской G, заменяем маску на символ T
             пример слов: ветер - гонец, первая буква Е будет иметь маску Y, поэтому заменяем ее на Т
             чтобы дальше можно было в фильтре символов, которые не на своих местах найти подходящее слово
             */
            if (newMask.contains("Y") && chars.toString().contains("G")) {
                for (int i = 0; i < oldMask.length; i++) {
                    for (int j = 0; j < charMask.length; j++) {
                        if (oldMask[i].equals("Y") && charMask[j].equals("G")) {
                            String regO = String.valueOf(newWord.charAt(i));
                            String regC = String.valueOf(list.get(x).charAt(j));
                            if (regO.equals(regC)) {
                                oldMask[i] = "T";
                            }
                        }
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (String ch : oldMask) {
                    sb.append(ch);
                }
                oldChars = sb.toString();
            }
            /*
             тут реализуем проверку символа, который может быть в слове из словаря дважды и иметь как
             маску G так и Y, если в слове находится такой символ и он не на своем месте, имеет маску Y
             и при этом находится на другой позиции под маской G, заменяем маску на символ J
             пример слов: арест - афера, вторая буква А будет иметь маску Y, поэтому заменяем ее на J
             чтобы дальше можно было в фильтре символов, которые не на своих местах найти подходящее слово
             */
            if (newMask.contains("G") && chars.toString().contains("Y")) {
                for (int i = 0; i < oldMask.length; i++) {
                    for (int j = 0; j < charMask.length; j++) {
                        if (oldMask[i].equals("G") && charMask[j].equals("Y")) {
                            String regO = String.valueOf(newWord.charAt(i));
                            String regC = String.valueOf(list.get(x).charAt(j));
                            if (regO.equals(regC)) {

                                charMask[j] = "J";

                            }
                        }
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (String ch : charMask) {
                    sb.append(ch);
                }
                newChars = sb.toString();
            }


            /*
            Вызываем сами методы и передаем новые подходящие символы через мапу
             */

            HashMap<String, String> mapa = new HashMap<>();
            greyChar(newChars, oldChars, wordGuess, newWord, mapa);
            yellowChar(oldChars, newWord, mapa);
            greenChar(oldChars, newWord, mapa);



            // считываем слова из отфильтрованногой мапы и записываем в список
            for (Map.Entry<String, String> entry1 : mapa.entrySet()) {
                String s = (String) entry1.getKey();
                String c = (String) entry1.getValue();
                arrw.add(c);

            }

        }
        return arrw;
    }

    public static ArrayList<String> inertArr = new ArrayList<>();

    public static void greyChar(String chars, String newMask, String wordGuess, String newWord, HashMap<String, String> filtrarray) {
        /*
        сортируем все слова с маской N таким образом, что отбираются именно индексы этих символов
        после чего берутся сами символы, затем берется слово из списка и проверятся на наличие в нем
        выбранных символов, которые не должны присутствовать в слове. Если ни одного символа в слове
        не было, то оно добавляется в мапу.
         */

        String[] arrReg = new String[]{" ", " ", " ", " ", " "};
        String[] oldMask = newMask.split("");
        if (newMask.contains("N")) {
            for (int i = 0; i < oldMask.length; i++) {
                if (oldMask[i].equals("N")) {
                    arrReg[i] = String.valueOf(newWord.charAt(i));
                    inertArr.add(String.valueOf(newWord.charAt(i)));
                }
            }
        }
        int count = 0;
        for (String value : arrReg) {
            if (!wordGuess.contains(value)) {
                count++;
            }
        }
        int countInert = 0;
        if (count == arrReg.length) {
            for (String s : inertArr) {
                if (!wordGuess.contains(s)) {
                    countInert++;
                }
            }
            if (countInert == inertArr.size()){
                filtrarray.put(chars, wordGuess);
            }

        }


    }

    public static void yellowChar(String newMask, String newWord, HashMap<String, String> filtrarray) {


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

                /*
                находим символы в которых присутствует маска Y, записываем их в отдельные переменные
                и фильтруем по алфавиту, если данные символы оказались равны, то продолжаем фильтр
                уже по позициям данных символов, самое главное, чтобы данные символы не были на
                одинаковых позициях в предполагаемом слове и слове из списка

                */

                String[] arrReg = new String[]{" ", " ", " ", " ", " "};
                if (newMask.contains("Y") || newMask.contains("J") && k.contains("Y") || k.contains("T") ) {
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

                // сортируем по алфавиту
                Collections.sort(countYO);
                Collections.sort(countYC);


                //убираем повторяющиеся символы
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

                //сравниваем выражения
                if (newResultO.equals(newResultC) && !resultC.equals("")) {


                    for (int i = 0; i < arrReg.length; i++) {
                        //проверяем на схождение позиций
                        if (!arrReg[i].equals(guessWordArr[i])) {
                            count++;
                        }
                    }

                }
                //если не один символ не совпал по позиции, то продолжаем сортировку, в другом случае - удаляем из мапы
                if (count == 5) {
                    continue;
                } else {
                    filtrarray.remove(k);
                }

            } else {
                continue;
            }
        }
    }

    public static void greenChar(String newMask, String newWord, HashMap<String, String> filtrarray) {

        /*
        производим сортировку путем удаления всех масок, кроме G и фильтруем так все оставшиеся слова
        если слово не совпало по маске G, то удаляем его из мапы
         */

        for (Map.Entry<String, String> entry : filtrarray.entrySet()) {
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (newMask.contains("G")) {
                String[] arrMask = newMask.replace("N", "-").replace("Y", "-").replace("T", "-").split("");
                StringBuilder sv = new StringBuilder();
                for (String ch : arrMask) {
                    sv.append(ch);
                }
                String word3 = sv.toString();

                String[] arrMaskN1 = k.replace("N", "-").replace("Y", "-").replace("J", "-").split("");
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
    }



    public static String sortWeight( ArrayList<String> list) {
        /*
    Реализуем метод, который будет фильтровать полученные слова по весу символов
    и выводить их в порядке возрастания весовой зависимости, то есть, чем больше
    весит слово, тем оно будет первее
     */

        HashMap<String, Integer> weightList = new HashMap<>();

        for (String z : list) {

            int count = 0;
            String[] chars = z.split("");
            for (String aChar : chars) {
                switch (aChar) {
                    case ("о") -> count += 33;
                    case ("e") -> count += 32;
                    case ("а") -> count += 31;
                    case ("и") -> count += 30;
                    case ("н") -> count += 29;
                    case ("т") -> count += 28;
                    case ("с") -> count += 27;
                    case ("р") -> count += 26;
                    case ("в") -> count += 25;
                    case ("л") -> count += 24;
                    case ("к") -> count += 23;
                    case ("м") -> count += 22;
                    case ("д") -> count += 21;
                    case ("п") -> count += 20;
                    case ("у") -> count += 19;
                    case ("я") -> count += 18;
                    case ("ы") -> count += 17;
                    case ("ь") -> count += 16;
                    case ("г") -> count += 15;
                    case ("з") -> count += 14;
                    case ("б") -> count += 13;
                    case ("ч") -> count += 12;
                    case ("й") -> count += 11;
                    case ("х") -> count += 10;
                    case ("ж") -> count += 9;
                    case ("ш") -> count += 8;
                    case ("ю") -> count += 7;
                    case ("ц") -> count += 6;
                    case ("щ") -> count += 5;
                    case ("э") -> count += 4;
                    case ("ф") -> count += 3;
                    case ("ъ") -> count += 2;
                    case ("ё") -> count += 1;
                }
            }
            weightList.put(z, count);
        }

        HashMap<String, Integer> sortedMap1 = weightList.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry1 : sortedMap1.entrySet()) {
            String c = entry1.getKey();
            result.append(c).append(" ");
        }


        return result.toString();
    }
}
