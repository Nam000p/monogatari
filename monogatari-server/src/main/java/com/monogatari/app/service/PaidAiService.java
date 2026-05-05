package com.monogatari.app.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.monogatari.app.dto.ai.AiRequest;
import com.monogatari.app.dto.ai.AiResponse;
import com.monogatari.app.entity.User;
import com.monogatari.app.enums.SubscriptionStatus;
import com.monogatari.app.exception.ApiRateLimitException;
import com.monogatari.app.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaidAiService extends BaseService {
    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    protected UserService getUserService() {
        return userService;
    }

    public AiResponse getChatResponse(AiRequest request) {
        User user = getCurrentUser();

        boolean hasActiveSubscription = subscriptionRepository.findFirstByUserIdOrderByCurrentPeriodEndDesc(user.getId())
                .map(sub -> (sub.getStatus() == SubscriptionStatus.ACTIVE || sub.getStatus() == SubscriptionStatus.CANCELED)
                        && sub.getCurrentPeriodEnd() != null
                        && sub.getCurrentPeriodEnd().isAfter(Instant.now()))
                .orElse(false);

        if (!hasActiveSubscription) {
            throw new AccessDeniedException("This archive is reserved for premium patrons only. Please check your subscription status.");
        }

        String systemText = """
                You are 'The Archivist', a sophisticated and mysterious librarian in 1920s England.
                Your goal is to help users navigate the Monogatari story archives.
                Style: Witty, formal, uses old-fashioned vocabulary, and maintains a noir vibe.
                Task: Suggest stories, summarize plots, or discuss literature within the vintage theme.
                """;

        String fullPrompt = "System Instruction: " + systemText + "\n\nUser Message: " + request.getMessage();

        try {
            Client client = new Client();
            GenerateContentResponse apiResponse = client.models.generateContent(
                    "gemini-2.0-flash",
                    fullPrompt,
                    null
            );

            AiResponse response = new AiResponse();
            response.setReply(apiResponse.text());
            return response;

        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("429")) {
                log.warn("AI Quota exceeded for user: {}", user.getEmail());
                throw new ApiRateLimitException("The Archivist is currently overwhelmed with requests. Please try again in a minute.");
            }

            log.error("AI Service Error: ", e);
            throw new RuntimeException("The archives are temporarily inaccessible: " + e.getMessage());
        }
    }
}