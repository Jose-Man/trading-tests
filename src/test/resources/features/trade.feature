@trade
Feature: Trades tests for two or three orders and a security

  @smoke
  Scenario: Basic trading Buy Sell
    Given one security "WSB" and two users "Diamond" and "Paper" exist
    When user "Diamond" puts a "buy" order for security "WSB" with a price of 101 and quantity of 50
    And user "Paper" puts a "sell" order for security "WSB" with a price of 100 and a quantity of 100
    Then a trade occurs with the price of 100 and quantity of 50

  @smoke
  Scenario: Basic trading Sell Buy
    Given one security "SEC" and two users "User1" and "User2" exist
    When user "User2" puts a "sell" order for security "SEC" with a price of 100 and a quantity of 100
    And user "User1" puts a "buy" order for security "SEC" with a price of 101 and quantity of 50
    Then a trade occurs with the price of 100 and quantity of 50

  @smoke
  Scenario: No trades occur for high price Sell Buy
    Given one security "NTR" and two users "User1" and "User2" exist
    When user "User2" puts a "sell" order for security "NTR" with a price of 100 and a quantity of 100
    And user "User1" puts a "buy" order for security "NTR" with a price of 99 and quantity of 50
    Then no trades occur

    
  Scenario Outline: Valid trading between two users and the same security
    Given one security "TST" and two users "Usr1" and "Usr2" exist
    When user "Usr1" puts a "<order1>" order for security "TST" with a price of <price1> and quantity of <quant1>
    And user "Usr2" puts a "<order2>" order for security "TST" with a price of <price2> and a quantity of <quant2>
    Then a trade occurs with the price of <trdPrice> and quantity of <trdQuant>

    Examples:
      | order1  | price1  | quant1  | order2  | price2  | quant2  | trdPrice  | trdQuant  |
      #| buy     | 101     | 50      | sell    | 100     | 100     | 100       | 50        |
      | buy     | 100     | 50      | sell    | 100     | 50      | 100       | 50        |
      | buy     | 100     | 101     | sell    | 100     | 100     | 100       | 100       |
      | buy     | 101     | 101     | sell    | 100     | 100     | 100       | 100       |
      | buy     | 100.25  | 50      | sell    | 100     | 100     | 100       | 50        |
      | buy     | 100.25  | 50      | sell    | 100.1   | 100     | 100.1     | 50        |
      | buy     | 1       | 50      | sell    | 0.001   | 100     | 0.001     | 50        |
      | sell    | 100     | 50      | buy     | 100     | 50      | 100       | 50        |
      #| sell    | 100     | 100     | buy     | 101     | 50      | 100       | 50        |
      | sell    | 100     | 100     | buy     | 100     | 101     | 100       | 100       |
      | sell    | 100     | 100     | buy     | 101     | 101     | 100       | 100       |
      | sell    | 100.25  | 50      | buy     | 101     | 50      | 100.25    | 50        |
      | sell    | 100     | 50      | buy     | 100.001 | 50      | 100       | 50        |
      | sell    | 1.001   | 50      | buy     | 1.01    | 50      | 1.001     | 50        |

    
  Scenario Outline: No trades occur sell price too high
    Given one security "TST2" and two users "Usr1" and "Usr2" exist
    When user "Usr1" puts a "<order1>" order for security "TST2" with a price of <price1> and quantity of <quant1>
    And user "Usr2" puts a "<order2>" order for security "TST2" with a price of <price2> and a quantity of <quant2>
    Then no trades occur

    Examples:
      | order1  | price1  | quant1  | order2  | price2  | quant2  |
      | buy     | 100     | 50      | sell    | 101     | 50      |
      | buy     | 1       | 50      | sell    | 1.001   | 50      |
      | sell    | 101     | 50      | buy     | 100     | 50      |
      | sell    | 99.001  | 50      | buy     | 99      | 50      |


  Scenario Outline: One user do sell and buy
    Given one security "ONE1" and one user "Usr1" exist
    When user "Usr1" puts a "<order1>" order for security "ONE1" with a price of 100 and quantity of 50
    And user "Usr1" puts a "<order2>" order for security "ONE1" with a price of 100 and a quantity of 50
    Then no trades occur

    Examples:
      | order1  | order2  |
      | buy     | sell    |
      | sell    | buy     |


  # Three users
  Scenario Outline: With 2 sell options buys with lowest valid price
    Given one security "THR1" and three users "<user1>" "<user2>" and "buyUser" exist
    When user "<user1>" puts a "sell" order for security "THR1" with a price of <price1> and quantity of 50
    And user "<user2>" puts a "sell" order for security "THR1" with a price of <price2> and a quantity of 50
    And user "buyUser" puts a "buy" order for security "THR1" with a price of <price3> and a quantity of 50
    Then a trade occurs with the price of <trdPrice> and quantity of 50 for buy user "buyUser" and sell user "<usrSell>"

    Examples:
      | user1 | user2 | price1  | price2  | price3  | trdPrice  | usrSell |
      | u1    | u2    | 5       | 2       | 10      | 2         | u2      |
      | u1    | u2    | 11.1    | 11.2    | 100     | 11.1      | u1      |
      | u1    | u2    | 18      | 16      | 17      | 16        | u2      |


 Scenario Outline: With 2 buy options sells to the first one valid
    Given one security "THR3" and three users "<user1>" "<user2>" and "sellUser" exist
    When user "<user1>" puts a "buy" order for security "THR3" with a price of <price1> and quantity of 50
    And user "<user2>" puts a "buy" order for security "THR3" with a price of <price2> and a quantity of 50
    And user "sellUser" puts a "sell" order for security "THR3" with a price of <price3> and a quantity of 50
    Then a trade occurs with the price of <trdPrice> and quantity of 50 for buy user "<usrBuy>" and sell user "sellUser"

    Examples:
      | user1 | user2 | price1  | price2  | price3  | trdPrice  | usrBuy  |
      | u1    | u2    | 50      | 20      | 10      | 10        | u1      |
      | u1    | u2    | 11.1    | 11.2    | 11      | 11        | u1      |
      | u1    | u2    | 2       | 7       | 4       | 4         | u2      |


  Scenario: One sell order could be covered by two buyers
    Given one security "THR1" and three users "Usr1" "Usr2" and "Usr3" exist
    When user "Usr1" puts a "buy" order for security "THR1" with a price of 50 and quantity of 50
    And user "Usr2" puts a "buy" order for security "THR1" with a price of 20 and a quantity of 50
    And user "Usr3" puts a "sell" order for security "THR1" with a price of 5 and a quantity of 100
    Then a trade occurs with the price of 5 and quantity of 50 for buy user "Usr1" and sell user "Usr3"
    And a trade occurs with the price of 5 and quantity of 50 for buy user "Usr2" and sell user "Usr3"
