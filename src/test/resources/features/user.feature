@user
Feature: User unitary tests

  # POST USER
  @smoke
  Scenario: Valid user creation without id
    When a non existent user is created with username "testName1" password "testPassword1" and without id
    Then api responds the username "testName1" password "testPassword1" in SHA256 hash and an id

  Scenario: Valid user creation with non existent id
    When a non existent user is created with username "usr" password "pw2" and id "2967efe9-3108-4d35-ae95-e1ec0ba2d2b1"
    Then api responds the username "usr" password "pw2" and a different id than "2967efe9-3108-4d35-ae95-e1ec0ba2d2b1"

  Scenario: Wrong user creation only with username
    When a non existent user is tried to be created with username "testName3" without password and without id
    Then user is not created

  Scenario: Wrong user creation only with password
    When a non existent user is tried to be created without username with password "testPasword4" and without id
    Then user is not created

  Scenario Outline: Wrong user creation with empty fields
    When a non existent user is created with username <userName> password <password> and without id
    Then user is not created

    Examples:
      | userName  | password  |
      | "test1"   | ""        |
      | ""        | "pass1"   |
      | ""        | ""        |
      | ""        | ""        |

  # GET USER BY ID
  @smoke
  Scenario: get existent user by id
    Given user with username "testName5" and password "testPassword5"
    When get by id is called with user id
    Then api responds the username "testName5" password "testPassword5" in SHA256 hash and id

  Scenario: get non existent user by id
    When get by id is called with non exist user id
    Then no user is retreived

  Scenario: udpate user by post with the same id
    Given user with username "testName6" and password "testPassword6"
    When post user is call with same id but userName "changedUsername6" and password "changedPassword6"
    And get by id is called with user id
    Then api responds the username "changedUsername6" password "changedPassword6" in SHA256 hash and id

  # GET ALL USERS
  @smoke
  Scenario: get an existent user from a list of users
    Given user with username "testName7" and password "testPassword7"
    When get list of users is called
    Then user "testName7" is in the list of users
