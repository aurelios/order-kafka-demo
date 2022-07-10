package br.com.kafka.order.infrastructure;

public interface Logging {

    String NAME_LOGGER_FULL = "logger_full";
    String NAME_REST_INTERCEPTPTOR_LOGGER = "logger_rest_interceptor";

    interface Field {
        String PURCHASE_ID = "purchaseId";
        String PERSON_ID = "personId";
        String USER_LOGIN = "userLogin";
        String INSCRIPTION_ID = "inscriptionId";
        String TOPIC = "topic";
        String PARTITION = "partition";
        String OFFSET = "offset";
        String BODY = "body";
        String REQUEST_ID = "requestId";
    }
}
