package org.poo;

import org.poo.AccountsSuite.Account;
import org.poo.TransactionsSuite.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the system with personal details, associated accounts, and transactions.
 */
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private List<Account> accounts;
    private List<Transaction> transactions;

    /**
     * Constructs a new User with the specified first name, last name, and email.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     */
    public User(final String firstName, final String lastName, final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    /**
     * Returns the user's first name.
     *
     * @return the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName the new first name for the user
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the user's last name.
     *
     * @return the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName the new last name for the user
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the user's email address.
     *
     * @return the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the new email address for the user
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Returns the list of accounts associated with the user.
     *
     * @return the list of accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Adds a new account to the user's list of accounts.
     *
     * @param account the account to add
     */
    public void addAccount(final Account account) {
        if (account != null) {
            accounts.add(account);
        }
    }

    /**
     * Adds a new transaction to the user's list of transactions.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(final Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    /**
     * Returns the list of transactions associated with the user.
     *
     * @return the list of transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
