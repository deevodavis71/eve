package com.sjd.eve.cqrs.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;
import lombok.Data;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 16:48
 * Description:
 */
@Document
@Data
public class AggregateEvent {

    @Id
    @NotNull
    private String eventId;

    @Field
    @NotNull
    private String aggregateId;

    @Field
    @NotNull
    private String eventType;

    @Field
    @NotNull
    private String eventData;

}
