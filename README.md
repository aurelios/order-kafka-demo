# order-kafka-demo

This project is a demo of how we can produce and consume messages of a kafka topic using avros.

## Getting Started

Follow  ```/order-producer/README.md``` and ```/order-consumer/README.md```

## UR'ls

#### Control Center
http://localhost:9021

# Kafka Comands
Connect on CLI of zookeeper and run comand bellow.

```kafka-topics --zookeeper localhost:2181 --topic order --describe```
1. You will see the broker leader of each topic partition
2. Stop **broker-1** and **broker-2** and run the command again you will see kafka recovering and **broker-3** will become leader of all partitions.