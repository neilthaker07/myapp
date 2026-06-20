package com.demo.myapp.chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    // ChatRoom is the Mediator — one instance for all users, Spring manages it
    private final ChatRoom chatRoom = new ChatRoom();

    // JOIN - POST /api/chat/join?name=Alice&role=regular|admin
    @PostMapping("/join")
    public ResponseEntity<Map<String, String>> join(
            @RequestParam String name,
            @RequestParam(defaultValue = "regular") String role) {

        User user = role.equalsIgnoreCase("admin")
                ? new AdminUser(name, chatRoom)
                : new RegularUser(name, chatRoom);

        chatRoom.addUser(user);
        return ResponseEntity.ok(Map.of(
                "message", name + " joined as " + role,
                "activeUsers", chatRoom.getActiveUsers().toString()));
    }

    // LEAVE - DELETE /api/chat/leave?name=Alice
    @DeleteMapping("/leave")
    public ResponseEntity<Map<String, String>> leave(@RequestParam String name) {
        chatRoom.removeUser(name);
        return ResponseEntity.ok(Map.of("message", name + " left the chat"));
    }

    // BROADCAST - POST /api/chat/send?from=Alice&message=Hello
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> send(
            @RequestParam String from,
            @RequestParam String message) {

        // Users are stateless here — we reconstruct to call send()
        // In a real app, users would be session-scoped beans
        RegularUser sender = new RegularUser(from, chatRoom);
        sender.send(message);
        return ResponseEntity.ok(Map.of("sent", message, "from", from));
    }

    // PRIVATE MESSAGE - POST /api/chat/dm?from=Alice&to=Bob&message=Hey
    @PostMapping("/dm")
    public ResponseEntity<Map<String, String>> dm(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String message) {

        RegularUser sender = new RegularUser(from, chatRoom);
        sender.sendPrivate(message, to);
        return ResponseEntity.ok(Map.of("dm", message, "from", from, "to", to));
    }

    // MESSAGE LOG - GET /api/chat/messages
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages() {
        return ResponseEntity.ok(Map.of(
                "activeUsers", chatRoom.getActiveUsers(),
                "messages", chatRoom.getMessageLog()));
    }
}
