package br.com.kafka.order.application.core.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

@Data
public class OrderRequest {

    private String id;
    private BigDecimal total;
    private String login;

    public String getId() {
        return String.valueOf(hashCode()).replace("-", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderRequest that = (OrderRequest) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(total, that.total) &&
                Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, login);
    }
}
