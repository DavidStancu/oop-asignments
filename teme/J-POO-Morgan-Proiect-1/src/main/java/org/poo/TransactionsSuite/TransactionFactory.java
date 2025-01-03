package org.poo.TransactionsSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * A factory class for creating various types of transactions.
 * It manages the creation of transaction objects based on the provided tag and parameters.
 */
public class TransactionFactory {
    private static final List<Transaction> transactions = new ArrayList<>();

    /**
     * Creates a transaction of a specified type.
     *
     * @param tag the transaction tag that determines the type of transaction
     * @param params the parameters required to create the transaction
     * @return the created transaction object
     */
    public static Transaction createTransaction(final TransactionTag tag, final Object... params) {
        Transaction transaction = null;

        switch (tag) {
            case ACCT_CREATED -> {
                int timestamp = (int) params[0];
                transaction = new AccountCreated(timestamp);
            }
            case TRANSFER -> {
                int timestamp = (int) params[0];
                String description = (String) params[1];
                String senderIBAN = (String) params[2];
                String receiverIBAN = (String) params[3];
                String amount = (String) params[4];
                String transferType = (String) params[5];
                String currency = (String) params[6];
                return new TransferType(timestamp, description,
                        senderIBAN, receiverIBAN, amount, transferType, currency);
            }
            case NO_FUNDS -> {
                int timestamp = (int) params[0];
                String description = (String) params[1];
                transaction = new NoFunds(timestamp, description);
            }
            case CARD_CREATED -> {
                int timestamp = (int) params[0];
                String accountIBAN = (String) params[1];
                String cardNumber = (String) params[2];
                String cardHolder = (String) params[3];
                transaction = new CardCreated(timestamp, accountIBAN, cardNumber, cardHolder);
            }
            case CARD_DELETED -> {
                int timestamp = (int) params[0];
                String accountIBAN = (String) params[1];
                String cardNumber = (String) params[2];
                String cardHolder = (String) params[3];
                transaction = new CardDeleted(timestamp, accountIBAN, cardNumber, cardHolder);
            }
            case ONLN_PAYMENT -> {
                int timestamp = (int) params[0];
                String description = (String) params[1];
                double amount = Double.parseDouble(params[2].toString());
                String commerciant = (String) params[3];
                transaction = new OnlinePayment(timestamp, description, amount, commerciant);
            }
            case CARD_STAT -> {
                int timestamp = (int) params[0];
                String description = (String) params[1];
                transaction = new CardStatus(timestamp, description);
            }
            case SPLIT_PAY -> {
                int timestamp = (int) params[0];
                List<String> participants = (List<String>) params[1];
                double totalAmount = Double.parseDouble(params[2].toString());
                String currency = (String) params[3];
                transaction = new SplitPay(timestamp, participants, totalAmount, currency);
            }
        }

        if (transaction != null) {
            transactions.add(transaction);
        }

        return transaction;
    }

    /**
     * Returns the list of all created transactions.
     *
     * @return a list of all transactions
     */
    public static List<Transaction> getTransactions() {
        return transactions;
    }
}
