package co.ceiba.transfers.infrastructure.adapter.out.notification;

import co.ceiba.transfers.domain.port.out.NotificationSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Simulates an SMS gateway by printing to the console. */
public class ConsoleNotificationSender implements NotificationSender {

    private final List<String> sent = new ArrayList<>();

    @Override
    public void send(String accountId, String message) {
        String sms = "[SMS -> " + accountId + "] " + message;
        sent.add(sms);
        System.out.println(sms);
    }

    @Override
    public List<String> sentMessages() {
        return Collections.unmodifiableList(sent);
    }
}
