package com.sjd.eve.cqrs.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;
import lombok.Data;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 16:49
 * Description:
 */
@Document
@Data
public class Aggregate {

    @Id
    @NotNull
    private String aggregateId;

    @Field
    @NotNull
    private String aggregateType;

}
