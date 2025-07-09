package cz.cvut.fel.omo.homeworks.refactor.transaction;

import cz.cvut.fel.omo.homeworks.common.client.LegacyPaymentClient;
import cz.cvut.fel.omo.homeworks.common.session.UserSession;

import java.util.Optional;

public class LegacyTransactionSystem extends AbstractTransactionSystem {

    private final LegacyPaymentClient legacyPaymentClient = new LegacyPaymentClient();
    private final UserSession userSession = new UserSession();

    public LegacyTransactionSystem(Long totalAmount, String currencyCode) {
        super(totalAmount, currencyCode);
    }

    @Override
    protected Optional<String> buildTransaction() {
        if (currencyCode == null || totalAmount == null) {
            return Optional.empty();
        }

        String transactionData = currencyCode + ";" + totalAmount;
        return Optional.of(transactionData);
    }
    @Override
    protected String execute(String transactionData) {
        String senderIP = userSession.getIP();
        return legacyPaymentClient.execute(transactionData, senderIP);
    }

}