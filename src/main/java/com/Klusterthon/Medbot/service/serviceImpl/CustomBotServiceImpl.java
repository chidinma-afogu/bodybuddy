package com.Klusterthon.Medbot.service.serviceImpl;

import com.Klusterthon.Medbot.dto.request.ChatGptRequest;
import com.Klusterthon.Medbot.dto.response.ChatGptResponse;
import com.Klusterthon.Medbot.service.ChatBotService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CustomBotServiceImpl implements ChatBotService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;

    @Override
    public String chat(String prompt){
        ChatGptRequest request=new ChatGptRequest(model, prompt);
        ChatGptResponse chatGptResponse = restTemplate.postForObject(apiURL, request, ChatGptResponse.class);
        assert chatGptResponse != null;
        return chatGptResponse.getChoices().get(0).getMessage().getContent();
}
}
