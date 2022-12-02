package com.osttra.fx.blockstream.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Customer.
 */
@Document(collection = "customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("customer_legal_entity")
    private String customerLegalEntity;

    @Field("customer_hash_code")
    private String customerHashCode;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Customer id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerLegalEntity() {
        return this.customerLegalEntity;
    }

    public Customer customerLegalEntity(String customerLegalEntity) {
        this.setCustomerLegalEntity(customerLegalEntity);
        return this;
    }

    public void setCustomerLegalEntity(String customerLegalEntity) {
        this.customerLegalEntity = customerLegalEntity;
    }

    public String getCustomerHashCode() {
        return this.customerHashCode;
    }

    public Customer customerHashCode(String customerHashCode) {
        this.setCustomerHashCode(customerHashCode);
        return this;
    }

    public void setCustomerHashCode(String customerHashCode) {
        this.customerHashCode = customerHashCode;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return id != null && id.equals(((Customer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", customerLegalEntity='" + getCustomerLegalEntity() + "'" +
            ", customerHashCode='" + getCustomerHashCode() + "'" +
            "}";
    }
}
