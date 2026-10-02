package co.ceiba.transfers.infrastructure.config;

import co.ceiba.transfers.application.decorator.AuditTrailDecorator;
import co.ceiba.transfers.application.decorator.DailyLimitDecorator;
import co.ceiba.transfers.application.decorator.FraudScreeningDecorator;
import co.ceiba.transfers.application.decorator.GmfTaxDecorator;
import co.ceiba.transfers.application.decorator.InterbankFeeDecorator;
import co.ceiba.transfers.application.decorator.NotificationDecorator;
import co.ceiba.transfers.application.service.BasicTransferService;
import co.ceiba.transfers.domain.port.in.TransferMoneyUseCase;
import co.ceiba.transfers.domain.port.out.AccountRepository;
import co.ceiba.transfers.domain.port.out.AuditLog;
import co.ceiba.transfers.domain.port.out.NotificationSender;

import java.util.List;

/**
 * Builds the decorator chain at runtime. The list is ordered from the outermost
 * layer to the innermost one, so it is wrapped starting from the end.
 */
public class TransferChainFactory {

    private final AccountRepository accountRepository;
    private final AuditLog auditLog;
    private final NotificationSender notificationSender;

    public TransferChainFactory(AccountRepository accountRepository, AuditLog auditLog,
                                NotificationSender notificationSender) {
        this.accountRepository = accountRepository;
        this.auditLog = auditLog;
        this.notificationSender = notificationSender;
    }

    public TransferMoneyUseCase build(List<DecoratorType> outermostFirst) {
        TransferMoneyUseCase chain = new BasicTransferService(accountRepository);
        for (int i = outermostFirst.size() - 1; i >= 0; i--) {
            chain = wrap(outermostFirst.get(i), chain);
        }
        return chain;
    }

    private TransferMoneyUseCase wrap(DecoratorType type, TransferMoneyUseCase inner) {
        return switch (type) {
            case FRAUD -> new FraudScreeningDecorator(inner, accountRepository);
            case DAILY_LIMIT -> new DailyLimitDecorator(inner, accountRepository);
            case ACH_FEE -> new InterbankFeeDecorator(inner, accountRepository);
            case GMF -> new GmfTaxDecorator(inner, accountRepository);
            case AUDIT -> new AuditTrailDecorator(inner, auditLog);
            case NOTIFICATION -> new NotificationDecorator(inner, notificationSender);
        };
    }
}
