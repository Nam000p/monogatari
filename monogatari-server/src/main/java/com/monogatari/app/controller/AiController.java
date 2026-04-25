package com.monogatari.app.controller;

import com.monogatari.app.dto.ai.AiRequest;
import com.monogatari.app.dto.ai.AiResponse;
import com.monogatari.app.service.PaidAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final PaidAiService paidAiService;

    @PostMapping("/chat")
    public ResponseEntity<AiResponse> handleAiChat(@RequestBody AiRequest request) {
        AiResponse response = paidAiService.getChatResponse(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}