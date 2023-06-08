package com.wordle.game.controllers;

import com.wordle.game.settingsGame.DataTransfer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/echo")
public class GameController {

    private final DataTransfer dataTransfer;

    public GameController(DataTransfer dataTransfer) {
        this.dataTransfer = dataTransfer;
    }

    @GetMapping
    public String home(@RequestParam String model){
        return dataTransfer.check(model);
    }


}
