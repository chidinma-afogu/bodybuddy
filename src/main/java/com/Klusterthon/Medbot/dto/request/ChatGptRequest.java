package com.Klusterthon.Medbot.dto.request;

import com.Klusterthon.Medbot.dto.response.ChatGptMessage;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
//@Builder
public class ChatGptRequest {
    private String model;
    private List<ChatGptMessage> messages;

    public ChatGptRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new ChatGptMessage("user",prompt));
    }
}
