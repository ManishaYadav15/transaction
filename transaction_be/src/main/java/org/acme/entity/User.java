package org.acme.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class to store user data.
 */
@MongoEntity(collection = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends PanacheMongoEntity {
    private String username;
    private String password;
}
