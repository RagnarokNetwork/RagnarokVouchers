package net.ragnaroknetwork.vouchers;

public class Utils {
    public static String getFormatted(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;

        StringBuilder builder = new StringBuilder();

        if (hours > 0)
            builder.append(hours).append(" h ");
        if (minutes > 0)
            builder.append(minutes).append(" m ");
        if (seconds > 0)
            builder.append(seconds).append(" s ");

        return builder.toString();
    }
}
