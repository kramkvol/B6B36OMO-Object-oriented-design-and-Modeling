package cz.cvut.fel.omo.homeworks.refactor.transaction;

import java.util.Optional;

public abstract class AbstractTransactionSystem implements TransactionSystem {
    protected Long totalAmount;
    protected String currencyCode;

    public AbstractTransactionSystem(Long totalAmount, String currencyCode) {
        this.totalAmount = totalAmount;
        this.currencyCode = currencyCode;
    }

    protected abstract Optional<String> buildTransaction();
    protected abstract String execute(String transactionData);

    // Optional je speciální třída, která může obsahovat hodnotu, nebo být prázdná (tím nahrazuje použití null).
    // map(this::execute) spustí metodu execute, pokud Optional obsahuje data transakce.
    // orElse("Transaction execution failed.") vrátí zprávu o neúspěchu, pokud Optional je prázdný.
    @Override
    public String executeTransaction() {
        Optional<String> transactionData = buildTransaction();
        if (transactionData.isEmpty()) {
            return "Transaction execution failed.";
        }
        return execute(transactionData.get());
    }

}
