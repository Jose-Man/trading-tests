package name.lattuada.trading.tests.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import name.lattuada.trading.model.EOrderType;
import name.lattuada.trading.model.dto.OrderDTO;
import name.lattuada.trading.model.dto.SecurityDTO;
import name.lattuada.trading.model.dto.TradeDTO;
import name.lattuada.trading.model.dto.UserDTO;
import name.lattuada.trading.tests.CucumberTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TradeSteps {

    private static final Logger logger = LoggerFactory.getLogger(CucumberTest.class);
    private final RestUtility restUtility;
    private final Map<String, SecurityDTO> securityMap;
    private final Map<String, UserDTO> userMap;
    private OrderDTO buyOrder;
    private final Map<String, OrderDTO> orderMap;
    private OrderDTO sellOrder;

    TradeSteps() {
        restUtility = new RestUtility();
        securityMap = new HashMap<>();
        userMap = new HashMap<>();
        orderMap = new HashMap<>();
    }

    @Given("one security {string} and two users {string} and {string} exist")
    public void oneSecurityAndTwoUsers(String securityName, String userName1, String userName2) {
        logger.trace("Got securityName = \"{}\"; username1 = \"{}\"; username2 = \"{}\"",
                securityName, userName1, userName2);
        createSecurity(securityName);
        createUser(userName1);
        createUser(userName2);
    }

    @Given("one security {string} and one user {string} exist")
    public void oneSecurityAndOneUser(String securityName, String userName) {
        logger.trace("Got securityName = \"{}\"; username1 = \"{}\"",
                securityName, userName);
        createSecurity(securityName);
        createUser(userName);
    }

    @Given("one security {string} and three users {string} {string} and {string} exist")
    public void oneSecurityAndThreeUsers(String securityName, String userName1, String userName2, String userName3) {
        logger.trace("Got securityName = \"{}\"; username1 = \"{}\"; username2 = \"{}\"; username3 = \"{}\"",
                securityName, userName1, userName2, userName3);
        createSecurity(securityName);
        createUser(userName1);
        createUser(userName2);
        createUser(userName3);
    }

    @When("user {string} puts a {string} order for security {string} with a price of {double} and quantity of {long}")
    @And("user {string} puts a {string} order for security {string} with a price of {double} and a quantity of {long}")
    public void userPutAnOrder(String userName, String orderType, String securityName, Double price, Long quantity) {
        logger.trace("Got username = \"{}\"; orderType = \"{}\"; securityName = \"{}\"; price = \"{}\"; quantity = \"{}\"",
                userName, EOrderType.valueOf(orderType.toUpperCase(Locale.ROOT)), securityName, price, quantity);
        assertTrue(String.format("Unknown user \"%s\"", userName),
                userMap.containsKey(userName));
        assertTrue(String.format("Unknown security \"%s\"", securityName),
                securityMap.containsKey(securityName));
        createOrder(userName,
                EOrderType.valueOf(orderType.toUpperCase(Locale.ROOT)),
                securityName,
                price,
                quantity);
    }

    @Then("a trade occurs with the price of {double} and quantity of {long}")
    public void aTradeOccursWithThePriceOfAndQuantityOf(Double price, Long quantity) {
        logger.trace("Got price = \"{}\"; quantity = \"{}\"",
                price, quantity);
        TradeDTO trade = restUtility.get("api/trades/orderBuyId/" + buyOrder.getId().toString()
                        + "/orderSellId/" + sellOrder.getId().toString(),
                TradeDTO.class);
        assertEquals("Price not expected", price, trade.getPrice());
        assertEquals("Quantity not expected", quantity, trade.getQuantity());
    }

    @Then("a trade occurs with the price of {double} and quantity of {long} for buy user {string} and sell user {string}")
    public void aTradeOccursWithThePriceOfAndQuantityOfForUsers(Double price, Long quantity, String userBuy, String userSell) {
        logger.trace("Got price = \"{}\"; quantity = \"{}\"",
                price, quantity);
        try {
            TradeDTO trade = restUtility.get("api/trades/orderBuyId/"
                            + orderMap.get(userBuy).getId().toString()
                            + "/orderSellId/" + orderMap.get(userSell).getId().toString(),
                    TradeDTO.class);
            assertEquals("Price not expected", price, trade.getPrice());
            assertEquals("Quantity not expected", quantity, trade.getQuantity());
        }
        catch(HttpClientErrorException.NotFound ex){
            Assert.fail("Transaction not found");
        }
    }

    @Then("no trades occur")
    public void noTradesOccur() {
        assertThatThrownBy(() -> restUtility.get("api/trades/orderBuyId/" + buyOrder.getId().toString()
                        + "/orderSellId/" + sellOrder.getId().toString(),
                TradeDTO.class)).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    private void createUser(String userName) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userName);
        userDTO.setPassword(RandomStringUtils.randomAlphanumeric(64));
        UserDTO userReturned = restUtility.post("api/users",
                userDTO,
                UserDTO.class);
        userMap.put(userName, userReturned);
        logger.info("User created: {}", userReturned);
    }

    private void createSecurity(String securityName) {
        SecurityDTO securityDTO = new SecurityDTO();
        securityDTO.setName(securityName);
        SecurityDTO securityReturned = restUtility.post("api/securities",
                securityDTO,
                SecurityDTO.class);
        securityMap.put(securityName, securityReturned);
        logger.info("Security created: {}", securityReturned);
    }

    private void createOrder(String userName,
                             EOrderType orderType,
                             String securityName,
                             Double price,
                             Long quantity) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(userMap.get(userName).getId());
        orderDTO.setType(orderType);
        orderDTO.setSecurityId(securityMap.get(securityName).getId());
        orderDTO.setPrice(price);
        orderDTO.setQuantity(quantity);

        OrderDTO orderReturned = restUtility.post("/api/orders",
                orderDTO,
                OrderDTO.class);
        switch (orderType) {
            case BUY -> buyOrder = orderReturned;
            case SELL -> sellOrder = orderReturned;
            default -> logger.error("Unknown order type: {}", orderType);
        }
        orderMap.put(userName, orderReturned);
        logger.info("Order created: {}", orderReturned);
    }

}
