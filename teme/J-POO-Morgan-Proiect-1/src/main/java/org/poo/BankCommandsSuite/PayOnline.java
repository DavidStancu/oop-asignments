package org.poo.BankCommandsSuite;

import org.poo.AccountsSuite.Account;
import org.poo.BankTeller;
import org.poo.CardsSuite.Card;
import org.poo.Commerciant;
import org.poo.OutputBuilder;
import org.poo.TransactionsSuite.TransactionFactory;
import org.poo.TransactionsSuite.TransactionTag;
import org.poo.User;
import org.poo.fileio.CommandInput;

import java.util.List;
import java.util.Map;

import static org.poo.TransactionsSuite.TransactionTag.ONLN_PAYMENT;

/**
 * Represents a command for making an online payment using a user's card.
 * The payment is processed by checking the card's status, converting currencies if necessary,
 * and updating the account balance. A transaction is recorded for each payment attempt.
 */
public class PayOnline implements BankCommand {
    private final List<User> users;
    private final CommandInput commandInput;
    private final OutputBuilder outputBuilder;

    /**
     * Constructs a PayOnline command.
     *
     * @param users the list of users whose accounts may contain the card to be used for
     *             the payment
     * @param commandInput the input data containing the payment details
     * @param outputBuilder the output builder used for handling error messages
     *                     and transaction outputs
     */
    public PayOnline(final List<User> users, final CommandInput commandInput,
                     final OutputBuilder outputBuilder) {
        this.users = users;
        this.commandInput = commandInput;
        this.outputBuilder = outputBuilder;
    }

    /**
     * Executes the command to make an online payment.
     * The payment is made using the provided card and account information.
     * The account balance is updated accordingly, and a transaction is recorded.
     * Currency conversion is performed if the payment currency differs from the account currency.
     * If the card is frozen or funds are insufficient, appropriate error transactions are created.
     * If the card is not found, an error message is returned.
     */
    public void execute() {

        String email = commandInput.getEmail();
        String cardNumber = commandInput.getCardNumber();
        double amount = commandInput.getAmount();
        String currency = commandInput.getCurrency();
        String description = commandInput.getDescription();
        String commerciant = commandInput.getCommerciant();
        int timestamp = commandInput.getTimestamp();

        // Iterate through the users to find the matching email
        for (User user : users) {
            if (user.getEmail().trim().equalsIgnoreCase(email.trim())) {
                // Iterate through the accounts to find the matching card
                for (Account account : user.getAccounts()) {
                    for (Card card : account.getCards()) {
                        if (card.getCardNumber().equals(cardNumber)) {

                            double convertedAmount = amount;
                            // Check if the card is frozen
                            if (card.getStatus().equals("frozen")) {
                                user.addTransaction(TransactionFactory
                                        .createTransaction(TransactionTag.CARD_STAT,
                                                commandInput.getTimestamp(), "The card is frozen"));
                                return;
                            }

                            // Convert the currency if necessary
                            if (!account.getCurrency().equalsIgnoreCase(currency)) {
                                convertedAmount = BankTeller.convertCurrency(amount,
                                        currency, account.getCurrency());
                            }

                            // Check if the account has sufficient balance
                            if (account.getBalance() >= convertedAmount) {
                                account.setBalance(account.getBalance() - convertedAmount);

                                user.addTransaction(TransactionFactory
                                        .createTransaction(ONLN_PAYMENT,
                                                timestamp, description,
                                                convertedAmount, commerciant));

                                // Update the commerciant with the payment details
                                Map<String, Commerciant> commerciants = BankTeller
                                        .getCommerciants();
                                Commerciant currentComerciant = commerciants
                                        .getOrDefault(commerciant, new Commerciant(commerciant));
                                currentComerciant.addPayment(account.getIBAN(), convertedAmount);
                                commerciants.put(commerciant, currentComerciant);
                            } else {
                                user.addTransaction(TransactionFactory
                                        .createTransaction(TransactionTag.NO_FUNDS,
                                                timestamp, "Insufficient funds"));
                            }
                            return;
                        }
                    }
                }
            }
        }
        // If card is not found, print an error
        outputBuilder.payOnlineError("Card not found", timestamp);
    }
}
