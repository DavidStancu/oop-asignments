package org.poo.BankCommandsSuite;

import org.poo.AccountsSuite.Account;
import org.poo.BankTeller;
import org.poo.OutputBuilder;
import org.poo.TransactionsSuite.TransactionFactory;
import org.poo.TransactionsSuite.TransactionTag;
import org.poo.User;
import org.poo.fileio.CommandInput;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Command class responsible for processing a split payment across multiple accounts.
 * This class divides a total amount into equal parts and deducts the corresponding
 * amount
 * from each specified account. The transaction is recorded for each account involved.
 */
public class SplitPayment implements BankCommand {
    private final List<User> users;
    private final CommandInput commandInput;
    private final OutputBuilder outputBuilder;

    /**
     * Constructs a SplitPayment command.
     *
     * @param users the list of users in the system
     * @param commandInput the input containing the details for the split payment
     * @param outputBuilder the output builder used to format and print results
     */
    public SplitPayment(final List<User> users, final CommandInput commandInput,
                        final OutputBuilder outputBuilder) {
        this.users = users;
        this.commandInput = commandInput;
        this.outputBuilder = outputBuilder;
    }

    /**
     * Executes the SplitPayment command.
     * This method performs the following steps:
     * <ol>
     *     <li>Removes duplicate IBANs from the list of account IBANs.</li>
     *     <li>Checks if each account has sufficient funds for the split amount.</li>
     *     <li>If all accounts have sufficient funds, deducts the split amount from each
     *     account.</li>
     *     <li>Creates a transaction for each account involved in the split payment.</li>
     * </ol>
     */
    @Override
    public void execute() {
        List<String> accountIBANs = commandInput.getAccounts();
        double totalAmount = commandInput.getAmount();
        String currency = commandInput.getCurrency();
        int timestamp = commandInput.getTimestamp();

        // Remove duplicate IBANs from the list
        Set<String> uniqueIbanSet = new HashSet<>(accountIBANs);
        accountIBANs.clear();
        accountIBANs.addAll(uniqueIbanSet);

        int numberOfAccounts = accountIBANs.size();
        double splitAmount = totalAmount / numberOfAccounts;

        // Check if all accounts have sufficient funds
        for (String iban : accountIBANs) {
            boolean accountFound = false;
            for (User user : users) {
                Account account = BankTeller.findAccountByIBANOrAlias(user, iban);
                if (account != null) {
                    double amountToWithdraw = splitAmount;
                    if (!account.getCurrency().equalsIgnoreCase(currency)) {
                        amountToWithdraw = BankTeller.convertCurrency(splitAmount,
                                currency, account.getCurrency());
                    }

                    // If account balance is less than the amount to withdraw, return early
                    if (account.getBalance() < amountToWithdraw) {
                        return;
                    }
                    accountFound = true;
                    break;
                }
            }
            if (!accountFound) {
                return;
            }
        }

        // Deduct the split amount from each account and record the transaction
        for (String iban : accountIBANs) {
            for (User user : users) {
                Account account = BankTeller.findAccountByIBANOrAlias(user, iban);
                if (account != null) {
                    double amountToWithdraw = splitAmount;
                    if (!account.getCurrency().equalsIgnoreCase(currency)) {
                        amountToWithdraw = BankTeller.convertCurrency(splitAmount,
                                currency, account.getCurrency());
                    }

                    // Update account balance
                    account.setBalance(account.getBalance() - amountToWithdraw);

                    // Create and add a transaction for the split payment
                    user.addTransaction(TransactionFactory.createTransaction(
                            TransactionTag.SPLIT_PAY,
                            timestamp,
                            accountIBANs,
                            totalAmount / numberOfAccounts,
                            currency
                    ));
                }
            }
        }
    }
}
