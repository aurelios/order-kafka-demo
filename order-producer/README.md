# Order Producer

This project is a demo of how we can use a service to produce messages to a kafka topic using avros.

## Getting Started

```
docker compose up
```
You can access the Control Center http://localhost:9021/

## order-producer

Running the application
```
mvn spring-boot:run
```
[POST] To Create Order and send to Kafka
```
curl -X POST "http://localhost:8080/api/orders" -H "Content-Type: application/json" -d "{\"id\":5,\"total\": 10.0,\"login\": \"aurelio\"}"
```
Publishing on kafka topic ```order```
```
newOrderRequest=OrderRequest(id=648331741, total=10.0, login=aurelio)
kafkaProducerEvent key="null" topic="order" partition="1"
```