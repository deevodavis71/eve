package com.sjd.eve.cqrs.repository;

import java.util.List;

import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.data.couchbase.repository.CouchbasePagingAndSortingRepository;

import com.sjd.eve.cqrs.domain.Aggregate;
import com.sjd.eve.cqrs.domain.Event;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 17:14
 * Description:
 */
@N1qlPrimaryIndexed
@ViewIndexed(designDoc = "events")
public interface EventRepository extends CouchbasePagingAndSortingRepository<Event, String> {

    List<Event> findByAggregateIdOrderByEventId(String aggregateId);

}
