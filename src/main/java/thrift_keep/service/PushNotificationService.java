package thrift_keep.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import thrift_keep.model.PushToken;
import thrift_keep.model.UserAccount;
import thrift_keep.repository.PushTokenRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class PushNotificationService {

    private static final String EXPO_PUSH_URL =
            "https://exp.host/--/api/v2/push/send";

    private final PushTokenRepository pushTokenRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PushNotificationService(
            PushTokenRepository pushTokenRepository
    ) {
        this.pushTokenRepository = pushTokenRepository;
        this.restClient = RestClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    // =========================================================
    // REGISTER PUSH TOKEN
    // =========================================================

    public PushToken registerToken(
            UserAccount user,
            String token,
            String platform,
            String deviceName
    ) {

        System.out.println("=================================================");
        System.out.println("REGISTERING EXPO PUSH TOKEN");
        System.out.println("User: " + user.getEmail());
        System.out.println("Token: " + token);
        System.out.println("Platform: " + platform);
        System.out.println("Device: " + deviceName);
        System.out.println("=================================================");

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Push token is required"
            );
        }

        String cleanToken = token.trim();

        Optional<PushToken> existing =
                pushTokenRepository.findByToken(cleanToken);

        PushToken pushToken;

        if (existing.isPresent()) {

            pushToken = existing.get();

            System.out.println(
                    "Existing push token found. Re-activating."
            );

        } else {

            pushToken = new PushToken();

            pushToken.setToken(cleanToken);

            System.out.println(
                    "Creating new push token."
            );
        }

        pushToken.setUser(user);
        pushToken.setPlatform(platform);
        pushToken.setDeviceName(deviceName);
        pushToken.setActive(true);

        PushToken saved =
                pushTokenRepository.save(pushToken);

        System.out.println(
                "Push token saved with ID: "
                        + saved.getId()
        );

        return saved;
    }

    // =========================================================
    // REMOVE PUSH TOKEN
    // =========================================================

    public void removeToken(
            UserAccount user,
            String token
    ) {

        if (token == null || token.trim().isEmpty()) {
            return;
        }

        String cleanToken = token.trim();

        pushTokenRepository
                .findByToken(cleanToken)
                .ifPresent(pushToken -> {

                    if (pushToken.getUser() != null
                            && pushToken.getUser()
                            .getId()
                            .equals(user.getId())) {

                        pushToken.setActive(false);

                        pushTokenRepository.save(pushToken);

                        System.out.println(
                                "Push token deactivated: "
                                        + cleanToken
                        );
                    }
                });
    }

    // =========================================================
    // SEND TO ONE USER
    // =========================================================

    public void sendToUser(
            Long userId,
            String title,
            String body,
            Map<String, Object> data
    ) {

        System.out.println("=================================================");
        System.out.println("SENDING PUSH NOTIFICATION");
        System.out.println("User ID: " + userId);
        System.out.println("Title: " + title);
        System.out.println("Body: " + body);

        List<PushToken> tokens =
                pushTokenRepository
                        .findByUserIdAndActiveTrue(userId);

        System.out.println(
                "Active devices found: "
                        + tokens.size()
        );

        if (tokens.isEmpty()) {
            System.out.println(
                    "No active push tokens found."
            );
            System.out.println("=================================================");
            return;
        }

        for (PushToken token : tokens) {

            System.out.println(
                    "Sending to token: "
                            + token.getToken()
            );

            sendToToken(
                    token,
                    title,
                    body,
                    data
            );
        }

        System.out.println("=================================================");
    }

    // =========================================================
    // SEND TO MULTIPLE USERS
    // =========================================================

    public void sendToUsers(
            Collection<Long> userIds,
            String title,
            String body,
            Map<String, Object> data
    ) {

        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(userId ->
                        sendToUser(
                                userId,
                                title,
                                body,
                                data
                        )
                );
    }

    // =========================================================
    // SEND TO EXPO
    // =========================================================

    private void sendToToken(
            PushToken pushToken,
            String title,
            String body,
            Map<String, Object> data
    ) {

        try {

            Map<String, Object> payload =
                    new HashMap<>();

            payload.put(
                    "to",
                    pushToken.getToken()
            );

            payload.put(
                    "title",
                    title
            );

            payload.put(
                    "body",
                    body
            );

            payload.put(
                    "sound",
                    "default"
            );

            payload.put(
                    "channelId",
                    "default"
            );

            payload.put(
                    "priority",
                    "high"
            );

            if (data != null) {
                payload.put(
                        "data",
                        data
                );
            }

            System.out.println(
                    "Sending payload to Expo:"
            );

            System.out.println(payload);

            String rawResponse =
                    restClient.post()
                            .uri(EXPO_PUSH_URL)
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(payload)
                            .retrieve()
                            .body(String.class);

            System.out.println(
                    "Raw Expo response:"
            );

            System.out.println(
                    rawResponse
            );

            if (rawResponse == null
                    || rawResponse.isBlank()) {

                System.out.println(
                        "Expo returned an empty response."
                );

                return;
            }

            JsonNode root =
                    objectMapper.readTree(
                            rawResponse
                    );

            JsonNode dataNode =
                    root.get("data");

            if (dataNode == null
                    || dataNode.isNull()) {

                System.out.println(
                        "Expo response contains no data."
                );

                return;
            }

            /*
             * Expo may return:
             *
             * {
             *   "data": {
             *      "status": "ok",
             *      "id": "..."
             *   }
             * }
             *
             * or:
             *
             * {
             *   "data": [
             *      {
             *         "status": "ok",
             *         "id": "..."
             *      }
             *   ]
             * }
             *
             * Handle both formats.
             */

            if (dataNode.isArray()) {

                for (JsonNode ticketNode : dataNode) {

                    handleExpoTicket(
                            pushToken,
                            ticketNode
                    );
                }

            } else if (dataNode.isObject()) {

                handleExpoTicket(
                        pushToken,
                        dataNode
                );

            } else {

                System.out.println(
                        "Unexpected Expo data format: "
                                + dataNode
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "================================================="
            );

            System.err.println(
                    "EXPO PUSH FAILED"
            );

            System.err.println(
                    "Token: "
                            + pushToken.getToken()
            );

            System.err.println(
                    "Error: "
                            + e.getMessage()
            );

            e.printStackTrace();

            System.err.println(
                    "================================================="
            );
        }
    }

    // =========================================================
    // HANDLE EXPO TICKET
    // =========================================================

    private void handleExpoTicket(
            PushToken pushToken,
            JsonNode ticket
    ) {

        if (ticket == null
                || ticket.isNull()) {

            return;
        }

        String status =
                ticket.has("status")
                        ? ticket.get("status").asText()
                        : null;

        String message =
                ticket.has("message")
                        ? ticket.get("message").asText()
                        : null;

        System.out.println(
                "Expo ticket status: "
                        + status
        );

        if (message != null) {

            System.out.println(
                    "Expo ticket message: "
                            + message
            );
        }

        if ("ok".equalsIgnoreCase(status)) {

            System.out.println(
                    "================================================="
            );

            System.out.println(
                    "EXPO PUSH ACCEPTED"
            );

            System.out.println(
                    "Token: "
                            + pushToken.getToken()
            );

            System.out.println(
                    "================================================="
            );

            return;
        }

        if ("error".equalsIgnoreCase(status)) {

            JsonNode details =
                    ticket.get("details");

            String errorCode = null;

            if (details != null
                    && details.has("error")) {

                errorCode =
                        details.get("error")
                                .asText();
            }

            System.err.println(
                    "================================================="
            );

            System.err.println(
                    "EXPO PUSH ERROR"
            );

            System.err.println(
                    "Token: "
                            + pushToken.getToken()
            );

            System.err.println(
                    "Error code: "
                            + errorCode
            );

            System.err.println(
                    "Message: "
                            + message
            );

            System.err.println(
                    "================================================="
            );

            /*
             * Expo tells us that the device token is
             * no longer registered.
             *
             * Deactivate it so we don't keep trying
             * to send notifications to a dead token.
             */
            if ("DeviceNotRegistered"
                    .equalsIgnoreCase(errorCode)) {

                pushToken.setActive(false);

                pushTokenRepository.save(
                        pushToken
                );

                System.out.println(
                        "Push token marked inactive because "
                                + "Expo reported DeviceNotRegistered."
                );
            }
        }
    }
}
