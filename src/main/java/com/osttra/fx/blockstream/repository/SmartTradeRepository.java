package com.osttra.fx.blockstream.repository;

import com.osttra.fx.blockstream.domain.SmartTrade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the SmartTrade entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SmartTradeRepository extends ReactiveMongoRepository<SmartTrade, String> {
    @Query("{}")
    Flux<SmartTrade> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    Flux<SmartTrade> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Mono<SmartTrade> findOneWithEagerRelationships(String id);
}
