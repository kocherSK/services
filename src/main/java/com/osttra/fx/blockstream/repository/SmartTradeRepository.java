package com.osttra.fx.blockstream.repository;

import com.osttra.fx.blockstream.domain.SmartTrade;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the SmartTrade entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SmartTradeRepository extends ReactiveMongoRepository<SmartTrade, String> {}
