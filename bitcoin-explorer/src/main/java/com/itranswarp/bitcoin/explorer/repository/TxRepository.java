package com.itranswarp.bitcoin.explorer.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.itranswarp.bitcoin.explorer.domain.TxEntity;

public interface TxRepository extends CrudRepository<TxEntity, String> {

	/**
	 * Find tx by block hash and order by tx index.
	 */
	List<TxEntity> findByBlockHashOrderByTxIndex(String blockHash);
}
