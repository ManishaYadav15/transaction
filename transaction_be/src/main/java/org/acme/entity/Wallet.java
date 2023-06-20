package org.acme.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class to store wallet data.
 */
@MongoEntity(collection = "wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet extends PanacheMongoEntity {
    private String username;
    private double balance;
}
