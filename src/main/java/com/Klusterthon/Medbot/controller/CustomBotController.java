package com.Klusterthon.Medbot.controller;

import com.Klusterthon.Medbot.service.ChatBotService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/bot")
@AllArgsConstructor
public class CustomBotController {
    private ChatBotService chatBotService;
    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt){
        return chatBotService.chat(prompt);
    }

}
