# Simple Trading Application Tests

These are the tests for [simple trading application](https://github.com/aldialimucaj/code-challenge).

## Configuration properties
base URL can be changed in name/lattuada/trading/test/resources/configuration.properties

## DataBase
Has been added the foreign keys:
* FK_ORDERS_SECURITIES
* FK_ORDERS_USERS
* FK_TRADES_ORDERS_BUY
* FK_TRADES_ORDERS_SELL

And constraints:
* CK_ORD_TYPE_BUY_SELL: ORDERS.ORD_TYPE only admits BUY or SELL
* CK_ORD_PRICE_HIGHER0: ORDERS.PRICE only admits positive values
* CK_ORD_QUANTITY_HIGHER0: ORDERS.QUANTITY only admits positive values
* CK_TRD_PRICE_HIGHER0: TRADES.PRICE only admits positive values
* CK_TRD_QUANTITY_HIGHER0: TRADES.QUANTITY only admits positive values

The script for this is DBCommands.sql

## Bugs found
First of all, I'm assuming that when any POST is called with a non-existent id,
this id should be omitted and a new one is assigned to the new entity.

* __BUG1__: Trades occur with the buy quantity even if this is higher than the one in the sell order
  * Test: _"Valid trading between two users and the same security"_
  1. User1 puts a buy order with a price of 100 and quantity of 101  
  2. User2 puts a sell order with a price of 100 and quantity of 50  
  3. a trade occurs with quantity 101, should be an error because this quantity has not been put up for sale.  
  This also happens is the sell order appears before the buy one.

* __BUG2__: If the same user creates a buy order and a sell order valid for it then a trade occurs
  * Test: _"One user do sell and buy"_
  * I assume that should not be admitted a trade with all orders of the same user.  
  This also happens is the sell order appears before the buy one.

* __BUG3__: If there are two buy orders and appears a sell one that is valid only for the second buy option,
then there is no transaction
  * Test: _"With 2 buy options sells to the first one valid"_
  1. Appears a buy order with price 50 and quantity 50
  2. Appears another buy order with price of 200 and quantity of 50
  3. Appears a sell order with price 100 and quantity 50  
  There are no trades, should generate a trade with the second buy order

* __BUG4__: If there are two buy orders and appears a sell one that could cover both at the same time then only occurs the first transaction
  * Test: "One sell order could be covered by two buyers"
  1. There are two buy orders with price 100 and quantity 50,
  2. appears a buy option with price 100 and quantity 100,    
  I assume that should be trade for both buyers, but only happens with the first one.
