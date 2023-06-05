package com.wordle.game.controllers;

import com.wordle.game.dameDao.GameDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/echo")
public class GameController {

    private final GameDao gameDao;

    public GameController(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @GetMapping
    public String home(@RequestParam String model){
        return gameDao.check(model);
    }


}
