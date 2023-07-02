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
Console's LOG on instance 1
```
Adding newly assigned partitions: order-2, order-0, order-1
order: partitions assigned: [order-2, order-0, order-1]
```
## Starting instance 2

Console's LOG on instance 1
```
order: partitions revoked: [order-2, order-0, order-1]
Adding newly assigned partitions: order-0, order-1
partitions assigned: [order-0, order-1]
```

Console's LOG on instance 2
```
order: partitions assigned: [order-2]
```

## Starting instance 3

Console's LOG on instance 1
```
Revoke previously assigned partitions order-0, order-1
order: partitions revoked: [order-0, order-1]
Adding newly assigned partitions: order-1
order: partitions assigned: [order-1]
```

Console's LOG on instance 2
```
Revoke previously assigned partitions order-2
Adding newly assigned partitions: order-2
order: partitions assigned: [order-2]
```

Console's LOG on instance 3
```
order: partitions assigned: [order-0]
```

## Cheking information about consumer and partitions
```
docker exec -it broker-1 bash
kafka-consumer-groups --bootstrap-server=localhost:9092 --group=order --describe
```
Output
```
GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                                    HOST            CLIENT-ID
order           order           0          6               6               0               order-consumer:16:29:04-0-70708cc1-7c5a-48bb-94bf-9ec3a4507cd2 /172.18.0.1     order-consumer:16:29:04-0
order           order           2          2               2               0               order-consumer:16:29:53-0-7dafc80a-0bac-4b48-a5d5-6341d01ecdcb /172.18.0.1     order-consumer:16:29:53-0
order           order           1          2               2               0               order-consumer:16:29:26-0-36072e3c-cd03-42ca-9846-5ee9008e194e /172.18.0.1     order-consumer:16:29:26-0
```