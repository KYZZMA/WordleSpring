package com.wordle.game.settingsGame;

import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class DataTransfer {


    ArrayList<String> guesslist = new ArrayList<>();
    ArrayList<String> list = DataLayer.scannerWord(guesslist);
    private final String guess = null;
    String puzzleWord = DataLayer.randomPuzzleString(list, guess);

    public String check(String model) {
        /*
        В этом методе реализуется формирование словаря и передача его
        в следующий метод, а также передача рандомного слова, которое
        будет отгадываться.
         */
        return ProgramLaunch.running(puzzleWord, list, model);
    }
}
