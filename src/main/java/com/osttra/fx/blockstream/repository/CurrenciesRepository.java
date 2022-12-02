package com.osttra.fx.blockstream.repository;

import com.osttra.fx.blockstream.domain.Currencies;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Currencies entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CurrenciesRepository extends ReactiveMongoRepository<Currencies, String> {}
