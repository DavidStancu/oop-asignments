package org.poo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.AccountsSuite.Account;
import org.poo.BankCommandsSuite.*;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BankTeller {
    private final OutputBuilder outputBuilder;
    private static List<User> users;
    private static List<ExchangeRates> exchangeRates;
    private static Map<String, Commerciant> commerciants;
    private int timestamp;

    public BankTeller() {
        this.outputBuilder = new OutputBuilder();
        this.users = new ArrayList<>();
        this.exchangeRates = new ArrayList<>();
        this.commerciants = new HashMap<>();
        this.timestamp = 0;
    }

    public void startDay(final ObjectInput inputData) {
        Utils.resetRandom();

        commerciants.clear();

        for (UserInput userInput : inputData.getUsers()) {
            User user = new User(userInput.getFirstName(),
                    userInput.getLastName(), userInput.getEmail());
            users.add(user);
        }

        for (ExchangeInput rateInput : inputData.getExchangeRates()) {
            ExchangeRates rate = new ExchangeRates();
            rate.setFrom(rateInput.getFrom());
            rate.setTo(rateInput.getTo());
            rate.setRate(rateInput.getRate());
            exchangeRates.add(rate);

            ExchangeRates inverseRate = new ExchangeRates();
            inverseRate.setFrom(rateInput.getTo());
            inverseRate.setTo(rateInput.getFrom());
            inverseRate.setRate(1 / rateInput.getRate());
            exchangeRates.add(inverseRate);



        }

        for (CommandInput command : inputData.getCommands()) {
            String commandName = command.getCommand();
            timestamp++;
            switch (commandName) {
                case "printUsers" -> {
                    PrintUsers printUsersCommand = new PrintUsers(users, outputBuilder, timestamp);
                    printUsersCommand.execute();
                }
                case "addAccount" -> {
                    AddAccount addAccountCommand = new AddAccount(users, command);
                    addAccountCommand.execute();
                }
                case "createCard", "createOneTimeCard" -> {
                    CreateCard createCardCommand = new CreateCard(users, command);
                    createCardCommand.execute();
                }
                case "addFunds" -> {
                    AddFunds addFundsCommand = new AddFunds(users, command);
                    addFundsCommand.execute();
                }
                case "deleteAccount" -> {
                    DeleteAccount deleteAccountCommand = new DeleteAccount(users,
                            command, outputBuilder);
                    deleteAccountCommand.execute();
                }
                case "deleteCard" -> {
                    DeleteCard deleteCardCommand = new DeleteCard(users, command);
                    deleteCardCommand.execute();
                }
                case "setMinimumBalance" -> {
                    SetMinimumBalance setMinimumBalanceCommand =
                            new SetMinimumBalance(users, command);
                    setMinimumBalanceCommand.execute();
                }
                case "payOnline" -> {
                    PayOnline payOnlineCommand = new PayOnline(users, command, outputBuilder);
                    payOnlineCommand.execute();
                }
                case "sendMoney" -> {
                    SendMoney sendMoneyCommand = new SendMoney(users, command);
                    sendMoneyCommand.execute();
                }
                case "setAlias" -> {
                    SetAlias setAliasCommand = new SetAlias(users, command);
                    setAliasCommand.execute();
                }
                case "printTransactions" -> {
                    PrintTransactions printTransactionsCommand =
                            new PrintTransactions(users, command, outputBuilder);
                    printTransactionsCommand.execute();
                }
                case "checkCardStatus" -> {
                    CheckCardStatus checkCardStatusCommand = new CheckCardStatus(users,
                            command, outputBuilder);
                    checkCardStatusCommand.execute();
                }
                case "splitPayment" -> {
                    SplitPayment splitPaymentCommand = new SplitPayment(users,
                            command, outputBuilder);
                    splitPaymentCommand.execute();
                }
                case "report" -> {
                    Report reportCommand = new Report(users, command, outputBuilder);
                    reportCommand.execute();
                }
                default -> {

                }
            }
        }

    }

    public ArrayNode getOutput() {
        return outputBuilder.getOutput();
    }

    public static double convertCurrency(final double amount,
                                         final String fromCurrency,
                                         final String toCurrency) {
        double ans = convertCurrencyRecursive(amount, fromCurrency,
                toCurrency, new ArrayList<>());
        return ans;

    }

    public static double convertCurrencyRecursive(final double amount,
                                                  final String fromCurrency,
                                                  final String toCurrency,
                                                  final List<String> visitedCurrencies) {
        for (ExchangeRates rate : exchangeRates) {
            if (rate.getFrom().equalsIgnoreCase(fromCurrency)
                    && rate.getTo().equalsIgnoreCase(toCurrency)) {
                return amount * rate.getRate();
            }
        }

        for (ExchangeRates rate : exchangeRates) {
            if (rate.getFrom().equalsIgnoreCase(fromCurrency)) {
                double convertedAmount = convertCurrencyRecursive(amount * rate.getRate(),
                        rate.getTo(), toCurrency, visitedCurrencies);
                if (convertedAmount != amount) {
                    return convertedAmount;
                }
            }
        }

        return amount;
    }

    public static User findUserByEmail(final String email) {
        for (User user : users) {
            if (user.getEmail().trim().equalsIgnoreCase(email.trim())) {
                return user;
            }
        }
        return null;
    }

    public static Account findAccountByIBANOrAlias(final User user, final String identifier) {
        for (Account account : user.getAccounts()) {
            if (account.getIBAN().equals(identifier)
                    || (account.getAlias() != null && account.getAlias()
                            .equalsIgnoreCase(identifier))) {
                return account;
            }
        }
        return null;
    }

    public static Map<String, Commerciant> getCommerciants() {
        return commerciants;
    }
}
