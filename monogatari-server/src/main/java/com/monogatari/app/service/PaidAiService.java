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
import java.util.concurrent.Semaphore;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaidAiService extends BaseService {

    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;

    private final Client client = new Client();

    private final Semaphore semaphore = new Semaphore(2);

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
            throw new AccessDeniedException(
                    "This archive is reserved for premium patrons only. Please check your subscription status.");
        }

        String systemText = """
                You are 'The Archivist', a sophisticated and mysterious librarian in 1920s England.
                Your goal is to help users navigate the Monogatari story archives.
                Style: Witty, formal, uses old-fashioned vocabulary, and maintains a noir vibe.
                Task: Suggest stories, summarize plots, or discuss literature within the vintage theme.
                """;

        String fullPrompt = "System Instruction: " + systemText +
                "\n\nUser Message: " + request.getMessage();

        boolean acquired = false;

        try {
            semaphore.acquire();
            acquired = true;

            log.warn("AI CALL >>> {} | thread={} | user={}",
                    Instant.now(),
                    Thread.currentThread().getName(),
                    user.getEmail()
            );

            GenerateContentResponse apiResponse = callWithRetry(fullPrompt);

            AiResponse response = new AiResponse();
            response.setReply(apiResponse.text());
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted");
        } catch (ApiRateLimitException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI Service Error", e);
            throw new RuntimeException("AI processing failed");
        } finally {
            if (acquired) {
                semaphore.release();
            }
        }
    }

    private GenerateContentResponse callWithRetry(String prompt) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                log.warn("AI CALL >>> attempt={} | thread={}",
                        i + 1,
                        Thread.currentThread().getName()
                );
                return client.models.generateContent(
                        "gemini-2.0-flash",
                        prompt,
                        null
                );
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("429")) {
                    long delay = (long) (Math.pow(2, i) * 1000 + Math.random() * 500);
                    log.warn("Retry {} after {} ms due to 429...",
                            i + 1,
                            delay
                    );
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e;
                }
            }
        }
        throw new ApiRateLimitException("AI service temporarily overloaded");
    }
}