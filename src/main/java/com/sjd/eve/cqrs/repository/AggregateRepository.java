package com.sjd.eve.cqrs.repository;

import java.util.List;

import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.data.couchbase.repository.CouchbasePagingAndSortingRepository;

import com.sjd.eve.cqrs.domain.Aggregate;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 17:14
 * Description:
 */
@N1qlPrimaryIndexed
@ViewIndexed(designDoc = "aggregates")
public interface AggregateRepository extends CouchbasePagingAndSortingRepository<Aggregate, String> {

}
