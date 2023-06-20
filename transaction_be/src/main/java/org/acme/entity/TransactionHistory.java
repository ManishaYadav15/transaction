package org.acme.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

/**
 * Entity class to store user transaction history.
 */
@MongoEntity(collection = "history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory extends PanacheMongoEntity {
    private String username;
    private double amount;
    private String operation;
    private Date date;
}
