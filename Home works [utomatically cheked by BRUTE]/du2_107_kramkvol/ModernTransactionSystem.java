package cz.cvut.fel.omo.homeworks.refactor.transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.omo.homeworks.common.client.ModernPaymentClient;
import cz.cvut.fel.omo.homeworks.common.model.ModernTransaction;

import java.util.Optional;
import java.util.UUID;

public class ModernTransactionSystem extends AbstractTransactionSystem{

    private final ModernPaymentClient modernPaymentClient = new ModernPaymentClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String surl;
    private final String furl;

    public ModernTransactionSystem(Long totalAmount, String currencyCode, String surl, String furl) {
        super(totalAmount, currencyCode);
        this.surl = surl;
        this.furl = furl;
    }
    @Override
    protected Optional<String> buildTransaction() {
        if (currencyCode == null || totalAmount == null || surl == null || furl == null) {
            return Optional.empty();
        }

        try {
            ModernTransaction transaction = new ModernTransaction()
                    .withExtOrderId(UUID.randomUUID().toString())
                    .withTotalAmount(totalAmount)
                    .withCurrencyCode(currencyCode)
                    .withSurl(surl)
                    .withFurl(furl);

            String transactionData = objectMapper.writeValueAsString(transaction);
            return Optional.of(transactionData);

        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    protected String execute(String transactionData) {
        return modernPaymentClient.execute(transactionData);
    }
}
