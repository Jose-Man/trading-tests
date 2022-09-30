package name.lattuada.trading.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import name.lattuada.trading.model.dto.UserDTO;
import name.lattuada.trading.tests.CucumberTest;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class UserSteps {

    private static final Logger logger = LoggerFactory.getLogger(CucumberTest.class);
    private final RestUtility restUtility;

    private HttpStatus responseStatus = HttpStatus.OK;
    private UserDTO userPost;
    private UserDTO userGet;

    private List<UserDTO> listUsers;

    UserSteps() {
        restUtility = new RestUtility();
    }

    @Given("user with username {string} and password {string}")
    @When("a non existent user is created with username {string} password {string} and without id")
    public void createUserWithUsernameAndPassword(String userName, String password) {
        createUser(userName, password);
    }

    @Then("api responds the username {string} password {string} in SHA256 hash and an id")
    public void responseHasCorrectUsernameAndPassword(String userName, String password) {
        validateUsernameAndPassword(userName, password, userPost);
        assertTrue("User \"%s\" does not have id: " + userName,
                getUserCreatedId() != null &&
                        !getUserCreatedId().toString().isEmpty());
    }

    @Then("api responds the username {string} password {string} and a different id than {string}")
    public void responseHasCorrectUsernamePasswordAndNewId(String userName, String password, String id) {
        validateUsernameAndPassword(userName, password, userPost);
        assertNotEquals(String.format("id for \"%s\" is not a new one", userName),
                getUserCreatedId(),
                UUID.fromString(id));
    }

    @When("a non existent user is created with username {string} password {string} and id {string}")
    public void createUserAllFields(String userName, String password, String id) {
        createUser(userName, password, UUID.fromString(id));
    }

    @When("a non existent user is tried to be created with username {string} without password and without id")
    public void tryCreateUserOnlyWithUsername(String userName) {
        tryCreateUserWithUsername(userName);
    }

    @When("a non existent user is tried to be created without username with password {string} and without id")
    public void tryCreateUserOnlyWithPassword(String password) {
        tryCreateUserWithPassword(password);
    }

    @Then("user is not created")
    public void noUserCreated() {
        assertEquals("response is not a badRequest", HttpStatus.BAD_REQUEST, responseStatus);
    }

    @When("get by id is called with user id")
    public void callByIdWithUserId() {
        callGetById(getUserCreatedId());
    }

    @When("get by id is called with non exist user id")
    public void callByIdWithWrongId() {
        callGetById(UUID.randomUUID());
    }

    @Then("api responds the username {string} password {string} in SHA256 hash and id")
    public void responseHasCorrectUsernamePasswordAndOwnId(String userName, String password) {
        validateUsernameAndPassword(userName, password, userGet);
        assertEquals(String.format("id for \"%s\" was not returned correctly", userName),
                getUserCreatedId(),
                getUserGottenId());
    }

    @Then("no user is retreived")
    public void noUserIsRetreived(){
        assertEquals("response is not notFound", HttpStatus.NOT_FOUND, responseStatus);
    }

    @When("post user is call with same id but userName {string} and password {string}")
    public void callPostUserWithSameIdAndNewUsernameAndPassword(String userName, String password) {
        createUser(userName, password, getUserCreatedId());
    }

    @When("get list of users is called")
    public void callGetListOfUsers() {
        callGetAllUsers();
    }

    @Then("user {string} is in the list of users")
    public void userIsInTheListOfUsers(String userName) {
        UUID userId = getUserCreatedId();
        assertTrue("User",
                listUsers.stream().anyMatch(usr -> usr.getId().equals(userId)));
    }

    public UUID getUserCreatedId(){
        return userPost.getId();
    }

    public UUID getUserGottenId(){
        return userGet.getId();
    }

    private void validateUsernameAndPassword(String userName, String password, UserDTO usr){
        assertEquals(String.format("userName for \"%s\" was not returned correctly", userName),
                userName, usr.getUsername());
        assertEquals(String.format("password for \"%s\" was not returned correctly", userName),
                DigestUtils.sha256Hex(password), usr.getPassword());
    }

    private void tryCreateUserWithUsername(String userName) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userName);
        createUser(userDTO);
    }

    private void tryCreateUserWithPassword(String password) {
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword(password);
        createUser(userDTO);
    }

    private void createUser(String userName, String password) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userName);
        userDTO.setPassword(password);
        createUser(userDTO);
    }

    private void createUser(String userName, String password, UUID id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userName);
        userDTO.setPassword(password);
        userDTO.setId(id);
        createUser(userDTO);
    }

    private void createUser(UserDTO userDTO) {
        try {
            userPost = restUtility.post("api/users",
                    userDTO,
                    UserDTO.class);
            logger.info("User created: {}", userPost);
        }
        catch (HttpClientErrorException.BadRequest ex){
            responseStatus = HttpStatus.BAD_REQUEST;
        }
    }

    private void callGetById(UUID id) {
        try {
            userGet = restUtility.get("api/users/" + id,
                    UserDTO.class);
            logger.info("User get: {}", userGet);
        }
        catch (HttpClientErrorException.NotFound ex){
            responseStatus = HttpStatus.NOT_FOUND;
        }
    }

    private void callGetAllUsers() {
        UserDTO[] lUsers = restUtility.get("api/users", UserDTO[].class);
        listUsers = Arrays.asList(lUsers);
    }
}
