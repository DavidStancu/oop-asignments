package org.poo.BankCommandsSuite;

import org.poo.AccountsSuite.Account;
import org.poo.BankTeller;
import org.poo.TransactionsSuite.TransactionFactory;
import org.poo.TransactionsSuite.TransactionTag;
import org.poo.User;
import org.poo.fileio.CommandInput;

import java.util.List;

import static org.poo.BankTeller.findAccountByIBANOrAlias;
import static org.poo.BankTeller.findUserByEmail;

/**
 * Command class responsible for handling money transfers between users.
 * This class processes the transfer of funds from a sender to a receiver,
 * including currency conversion if needed, and records the transaction for both users.
 */
public class SendMoney implements BankCommand {
    private final List<User> users;
    private final CommandInput commandInput;

    /**
     * Constructs a SendMoney command.
     *
     * @param users the list of users in the system
     * @param commandInput the input containing the details of the transaction
     */
    public SendMoney(final List<User> users, final CommandInput commandInput) {
        this.users = users;
        this.commandInput = commandInput;
    }

    /**
     * Executes the send money command.
     * This method processes a transfer from the sender to the receiver by
     * checking the sender's balance,
     * verifying the account details, converting the currency if necessary,
     * and updating the balances.
     * It also logs the transaction for both the sender and the receiver.
     */
    @Override
    public void execute() {
        String senderEmail = commandInput.getEmail();
        String senderIBAN = commandInput.getAccount();
        String receiverIBAN = commandInput.getReceiver();
        double amount = commandInput.getAmount();
        String description = commandInput.getDescription();
        int timestamp = commandInput.getTimestamp();

        User senderUser = findUserByEmail(senderEmail);
        if (senderUser == null) {
            return;
        }

        Account senderAccount = findAccountByIBANOrAlias(senderUser, senderIBAN);
        if (senderAccount == null) {
            return;
        }

        User receiverUser = null;
        Account receiverAccount = null;

        for (User user : users) {
                receiverAccount = findAccountByIBANOrAlias(user, receiverIBAN);
                if (receiverAccount != null) {
                    receiverUser = user;
                    break;
                }
        }

        if (receiverAccount == null) {
            return;
        }

        if (!senderAccount.getIBAN().equals(senderIBAN)
                || !receiverAccount.getIBAN().equals(receiverIBAN)) {
            return;
        }

        if (senderAccount.getBalance() < amount) {
            senderUser.addTransaction(TransactionFactory
                    .createTransaction(TransactionTag.NO_FUNDS,
                            timestamp, "Insufficient funds"));
            return;
        }

        double convertedAmount = amount;
        if (!senderAccount.getCurrency()
                .equalsIgnoreCase(receiverAccount.getCurrency())) {
            convertedAmount = BankTeller
                    .convertCurrency(amount, senderAccount.getCurrency(),
                            receiverAccount.getCurrency());
        }

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + convertedAmount);

        senderUser.addTransaction(TransactionFactory
                .createTransaction(
                        TransactionTag.TRANSFER, timestamp,
                        description, senderAccount.getIBAN(),
                        receiverAccount.getIBAN(), String.valueOf(amount),
                        "sent", senderAccount.getCurrency()));

        receiverUser.addTransaction(TransactionFactory
                .createTransaction(
                        TransactionTag.TRANSFER, timestamp,
                        description, receiverAccount.getIBAN(),
                        senderAccount.getIBAN(), String.valueOf(convertedAmount),
                        "received", receiverAccount.getCurrency()));
    }
}
