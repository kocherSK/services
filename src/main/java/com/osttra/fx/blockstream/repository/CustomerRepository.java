package com.osttra.fx.blockstream.repository;

import com.osttra.fx.blockstream.domain.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
    @Query("{}")
    Flux<Customer> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    Flux<Customer> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Mono<Customer> findOneWithEagerRelationships(String id);
}
