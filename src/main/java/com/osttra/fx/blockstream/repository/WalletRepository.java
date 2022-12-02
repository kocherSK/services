package com.osttra.fx.blockstream.repository;

import com.osttra.fx.blockstream.domain.Wallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Wallet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WalletRepository extends ReactiveMongoRepository<Wallet, String> {}
