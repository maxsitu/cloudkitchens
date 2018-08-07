# Cloud Kitchens Order Delivery System

## Description
This program is simulating the whole fulfillment of food orders in a kitchen-delivery system. It includes three main roles:
* **Order maker**: a role represents a kitchen produces orders.
* **Order taker**: a role represents a driver to pick up for delivery.
* **Order manager**: a role in the system manages order and allocating shelvs to put away the orders.

## How to build
```bash
./gradlew build
```

## How to run
```bash
ORDER_SERVICE_PROP_PATH='./src/main/conf/order-service.properties'
INPUT_JSON=~/Downloads/input.json
java -Dorder-service.properties=$ORDER_SERVICE_PROP_PATH  -jar build/libs/cloudkitchens-1.0.0.jar $INPUT_JSON
```

## Shelf strategies
* **ORDER_BY_VALUE**: If shelf of corresponding order's temperature is full and overflow shelf is also full, remove the order of least value from the both shelfs (the shelf with temperature, and overflow shelf).
* **ORDER_BY_EXPIRATION**: Instead of choosing the order of least value to be replaced with new one, this strategy is replacing the order with nearest expiration time.

## Configuration
Property file can be put in any path. The path of it should be declared in JVM argument _order-service.properties_.
* **shelfStrategy**: Name of shelf strategy, it can either be "_ORDER_BY_VALUE_" or "_ORDER_BY_EXPIRATION_".
* **quota.hot**: Shelf capacity of hot shelf.
* **quota.cold**: Shelf capacity of cold shelf.
* **quota.frozen**: Shelf capacity of frozen shelf.
* **quota.overflow**: Shelf capacity of overflow shelf.
* **orderTaker.meanWaitTimeMillis**: Orders are being picked up in time manner of poisson distribution. The time between each take of order is in poisson distribution. As per requirements, the mean value is 333 milliseconds (3 times a second).
* **orderMaker.freqPerSecond**: The frequency that kitchen is producing order.
