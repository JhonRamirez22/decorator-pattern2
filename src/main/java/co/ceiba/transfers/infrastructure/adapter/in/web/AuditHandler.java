package co.ceiba.transfers.infrastructure.adapter.in.web;

import co.ceiba.transfers.domain.port.out.AuditLog;
import co.ceiba.transfers.domain.port.out.NotificationSender;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

/** GET /api/audit: audit log entries and simulated SMS messages. */
public class AuditHandler extends JsonHandler {

    private final AuditLog auditLog;
    private final NotificationSender notificationSender;

    public AuditHandler(AuditLog auditLog, NotificationSender notificationSender) {
        this.auditLog = auditLog;
        this.notificationSender = notificationSender;
    }

    @Override
    protected Object handleRequest(HttpExchange exchange) {
        return Map.of("audit", auditLog.entries(), "sms", notificationSender.sentMessages());
    }
}
