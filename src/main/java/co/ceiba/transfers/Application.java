package co.ceiba.transfers;

import co.ceiba.transfers.domain.port.out.AccountRepository;
import co.ceiba.transfers.domain.port.out.AuditLog;
import co.ceiba.transfers.domain.port.out.NotificationSender;
import co.ceiba.transfers.infrastructure.adapter.in.web.AccountsHandler;
import co.ceiba.transfers.infrastructure.adapter.in.web.AuditHandler;
import co.ceiba.transfers.infrastructure.adapter.in.web.StaticFileHandler;
import co.ceiba.transfers.infrastructure.adapter.in.web.TransfersHandler;
import co.ceiba.transfers.infrastructure.adapter.out.memory.InMemoryAccountRepository;
import co.ceiba.transfers.infrastructure.adapter.out.memory.InMemoryAuditLog;
import co.ceiba.transfers.infrastructure.adapter.out.notification.ConsoleNotificationSender;
import co.ceiba.transfers.infrastructure.config.TransferChainFactory;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/** Wires the hexagon: creates the adapters and starts the HTTP server. */
public class Application {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        AccountRepository accountRepository = new InMemoryAccountRepository();
        AuditLog auditLog = new InMemoryAuditLog();
        NotificationSender notificationSender = new ConsoleNotificationSender();
        TransferChainFactory chainFactory = new TransferChainFactory(accountRepository, auditLog, notificationSender);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/accounts", new AccountsHandler(accountRepository));
        server.createContext("/api/transfers", new TransfersHandler(chainFactory));
        server.createContext("/api/audit", new AuditHandler(auditLog, notificationSender));
        server.createContext("/", new StaticFileHandler());
        server.start();

        System.out.println("Banco Ceiba running on http://localhost:" + PORT);
    }
}
