# Order Consumer

This project is a demo of how we can use consume messages from a kafka topic using avros.

## order-consumer

Running the application

```
mvn spring-boot:run
```

# Running multiple consumers
You can run more than one instance of the application with ```mvn spring-boot:run``` to note the distributions of partitions assigned to each instance

## Starting instance 1
instance 1
```
Adding newly assigned partitions: order-2, order-0, order-1
order: partitions assigned: [order-2, order-0, order-1]
```
## Starting instance 2

instance 1
```
order: partitions revoked: [order-2, order-0, order-1]
Adding newly assigned partitions: order-0, order-1
partitions assigned: [order-0, order-1]
```

instance 2
```
order: partitions assigned: [order-2]
```

## Starting instance 3

instance 1
```
Revoke previously assigned partitions order-0, order-1
order: partitions revoked: [order-0, order-1]
Adding newly assigned partitions: order-1
order: partitions assigned: [order-1]
```

instance 2
```
Revoke previously assigned partitions order-2
Adding newly assigned partitions: order-2
order: partitions assigned: [order-2]
```

instance 3
```
order: partitions assigned: [order-0]
```