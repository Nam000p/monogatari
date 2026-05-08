package com.monogatari.app.service;

import com.monogatari.app.dto.ai.AiRequest;
import com.monogatari.app.dto.ai.AiResponse;
import com.monogatari.app.entity.User;
import com.monogatari.app.enums.SubscriptionStatus;
import com.monogatari.app.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class PaidAiService extends BaseService {
    private final UserService userService;

    private final SubscriptionRepository subscriptionRepository;

    private final ChatClient chatClient;

    public PaidAiService(UserService userService,
                         SubscriptionRepository subscriptionRepository,
                         ChatClient.Builder chatClientBuilder) {
        this.userService = userService;
        this.subscriptionRepository = subscriptionRepository;

        this.chatClient = chatClientBuilder
                .defaultOptions(OpenAiChatOptions.builder()
                        .withModel("gemini-2.5-flash")
                        .withTemperature(0.7)
                        .build())
                .build();
    }

    @Override
    protected UserService getUserService() {
        return userService;
    }

    public AiResponse getChatResponse(AiRequest request) {
        User user = getCurrentUser();

        boolean hasActiveSubscription = subscriptionRepository
                .findFirstByUserIdOrderByCurrentPeriodEndDesc(user.getId())
                .map(sub -> (sub.getStatus() == SubscriptionStatus.ACTIVE
                        || sub.getStatus() == SubscriptionStatus.CANCELED)
                        && sub.getCurrentPeriodEnd() != null
                        && sub.getCurrentPeriodEnd().isAfter(Instant.now()))
                .orElse(false);

        if (!hasActiveSubscription) {
            throw new AccessDeniedException("This archive is reserved for premium patrons only.");
        }

        String systemInstruction = """
                You are 'The Archivist', a sophisticated and mysterious librarian in 1920s England.
                Your goal is to help users navigate the Monogatari story archives.
                Style: Witty, formal, uses old-fashioned vocabulary, and maintains a noir vibe.
                """;

        try {
            log.info("AI CALL >>> user={} | model=gemini-1.5-flash", user.getEmail());

            String reply = chatClient.prompt()
                    .system(systemInstruction)
                    .user(request.getMessage())
                    .call()
                    .content();

            AiResponse response = new AiResponse();
            response.setReply(reply);
            return response;

        } catch (NonTransientAiException e) {
            log.error("AI Quota Exceeded: {}", e.getMessage());
            throw new RuntimeException("The Archivist is currently overwhelmed. Please try again in 1 minute.");
        } catch (Exception e) {
            log.error("AI Error: ", e);
            throw new RuntimeException("The archive is temporarily unreachable.");
        }
    }
}