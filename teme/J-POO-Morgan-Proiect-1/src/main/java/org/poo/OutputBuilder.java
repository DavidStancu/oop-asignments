package org.poo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.UserInput;

import java.util.List;

public class OutputBuilder {
    private final ArrayNode output;
    public OutputBuilder() {
        this.output = new ObjectMapper().createArrayNode();
    }

    public void printUsers(List<User> users, int timestamp) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("command", "printUsers");
        ArrayNode usersArray = objectMapper.createArrayNode();

        for (User user : users) {
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("firstName", user.getFirstName());
            userNode.put("lastName", user.getLastName());
            userNode.put("email", user.getEmail());
            ArrayNode accountsArray = objectMapper.createArrayNode();

            if (user.getAccounts() != null && !user.getAccounts().isEmpty()) {
                for (Account account : user.getAccounts()) {
                    ObjectNode accountNode = objectMapper.createObjectNode();
                    accountNode.put("IBAN", account.getIBAN());
                    accountNode.put("balance", account.getBalance());
                    accountNode.put("currency", account.getCurrency());
                    ArrayNode cardsArray = objectMapper.createArrayNode();
                    if (account.getCards() != null && !account.getCards().isEmpty()) {
                        for (Card card : account.getCards()) {
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            cardNode.put("cardNumber", card.getCardNumber());
                            cardNode.put("status", card.getStatus());
                            cardsArray.add(cardNode);
                        }
                    }
                    accountNode.put("type", account.getAccountType());
                    accountNode.set("cards", cardsArray);
                    accountsArray.add(accountNode);
                }
            }

            userNode.set("accounts", accountsArray);
            usersArray.add(userNode);
        }

        outputNode.set("output", usersArray);
        outputNode.put("timestamp", timestamp);
        output.add(outputNode);
    }

    public void printDeleteAccountSuccess(int timestamp) {
        System.out.println("printOutput: ");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode outputNode = objectMapper.createObjectNode();

        outputNode.put("command", "deleteAccount");

        ObjectNode successNode = objectMapper.createObjectNode();
        successNode.put("success", "Account deleted");
        successNode.put("timestamp", timestamp);

        outputNode.set("output", successNode);
        outputNode.put("timestamp", timestamp);
        output.add(outputNode);
    }

    public void payOnlineError(String description, int timestamp) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode outputNode = objectMapper.createObjectNode();

        outputNode.put("command", "payOnline");

        ObjectNode errorNode = objectMapper.createObjectNode();
        errorNode.put("description", description);
        errorNode.put("timestamp", timestamp);

        outputNode.set("output", errorNode);

        outputNode.put("timestamp", timestamp);
        output.add(outputNode);
    }

    public ArrayNode getOutput() {
        return output;
    }
}
