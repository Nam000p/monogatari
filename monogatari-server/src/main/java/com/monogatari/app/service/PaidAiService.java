package com.monogatari.app.service;

import com.monogatari.app.dto.ai.AiRequest;
import com.monogatari.app.dto.ai.AiResponse;
import com.monogatari.app.entity.User;
import com.monogatari.app.enums.SubscriptionStatus;
import com.monogatari.app.exception.ApiRateLimitException;
import com.monogatari.app.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaidAiService extends BaseService {
    private final ChatModel chatModel;
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

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemPromptTemplate.createMessage();
        UserMessage userMessage = new UserMessage(request.getMessage());
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        try {
            String aiReply = chatModel.call(prompt).getResult().getOutput().getContent();

            AiResponse response = new AiResponse();
            response.setReply(aiReply);
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