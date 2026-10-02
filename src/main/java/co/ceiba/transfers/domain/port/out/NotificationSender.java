package co.ceiba.transfers.domain.port.out;

import java.util.List;

public interface NotificationSender {

    void send(String accountId, String message);

    List<String> sentMessages();
}
