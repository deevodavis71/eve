package com.sjd.eve.cqrs.core;

import java.util.UUID;

import lombok.Data;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 16:49
 * Description:
 */
@Data
abstract public class AggregateRoot {

    protected String aggregateId;

}
