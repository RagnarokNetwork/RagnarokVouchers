package net.ragnaroknetwork.vouchers;

public class ChatMessage {
    private final String message;

    public ChatMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
