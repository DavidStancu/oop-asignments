# OOP Assignment: J. POO Morgan - Phase 1

## Overview
The assignment simulates a banking system in Java, which undergoes various operations, such as creating accounts,
creating new cards, executing transactions towards commerciants or other accounts.

## Single issue classes
* User - the main hub for a singular user in the bank database/test input. Stores information about accounts with the
same email.
* ExchangeRates - holds the information about exchange rates from the test input.
* BankTeller - the main class where the commands from the input jackson file are parsed and the processed. it also
has a few other methods that help the searching for important bits, such as an user based on email or an account based
on the iban. also holds the logic behind the exchange rates conversion.
* OutputBuilder - represents a hub for various methods that output a certain message in the output jackson file.
* Main - the class from which the checker is initialised. It has been modified to start the command parsing process
and return the output.
* Commerciants - this class holds the logic for specific commerciants, as well as how much each account has paid for
its products. Due to the fact that spendingsReport has not yet been implemented, this class serves no purpose :[

## The Accounts Suite
This package holds the information behind user created accounts. It is pretty straightforward:
* Account interface - has the bare-bones abstract methods with which the ClassicAccount and SavingsAccount
will be implemented.
* ClassicAccount - standard issue account, can both get money and spend it. nothing special behind it.
* SavingsAccount - a savings account which can only receive money. it cannot make transactions.
* AccountFactory - a factory that can initialise both types of accounts

## The Cards Suite
This package holds the logic of implementation behind normal/classic cards and OneTime cards

## The Bank Commands Suite
This package contains a class for each command from the input file
* BankCommand interface: standard command design pattern interface. all commands
in this suite implement it differently
* printUsers: prints all users, as well as all their accounts and cards
* addAccount: adds an account to the designated user. the account has a randomised number
* addFunds: adds funds to the designated account
* createCard: creates a card for the designated user. the card has a randomised number
* deleteAccount: deletes the account, as well as all the account's cards
* deleteCard: deletes the designated card
* payOnline: the designated account makes a payment through the designated card
* sendMoney: the designated account makes a payment to another account. Accounts can be owned by the same user
* setMinimumBalance: sent the minimum balance for an account. Helps the checkCardStatus command
* setAlias: sets an alias for an account. Helps when both accounts that are involved in sendMoney
are owned by the same user
* splitPayment: splits a payment equally between multiple accounts
* printTransactions and Report: both similar commands, they generate a list of transactions. I have grouped
them together because they use the same arrayList of "transactions", which stores information about various actions
that take place during commands. While printTransactions prints all transactions that took place during the program, 
Report only prints the transactions that take place at the designated account, during the designated timestamps

## The Transactions Suite
This package contains various classes that store specific information about certain actions that take place during
the execution of commands. Be it errors, card creation or deletion, payments, information is stored inside the
transactions arrayList. All of them have the same structure, so all we need to talk about is:
* Transaction abstract class: this class holds the default information that all other Transaction child classes will
implement, with the moth important being the getDescription in most cases
* TransactionFactory: factory class for all the Transaction child classes. uses TransactionTags assigned for
each specific class to build its node
* TransactionTag enum class: this class holds the tags for each Transaction class. When OutputBuilder
or TransactionFactory needs to build a certain output or node, they will use this tag as guidance