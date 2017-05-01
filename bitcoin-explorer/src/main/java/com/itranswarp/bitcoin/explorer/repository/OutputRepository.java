package com.itranswarp.bitcoin.explorer.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.itranswarp.bitcoin.explorer.domain.OutputEntity;

public interface OutputRepository extends CrudRepository<OutputEntity, String> {

	/**
	 * Find by txout hash and order by txout index.
	 */
	List<OutputEntity> findByTxoutHashOrderByTxoutIndex(String txHash);

	/**
	 * Find by txin hash and order by txin index.
	 */
	List<OutputEntity> findByTxinHashOrderByTxinIndex(String txHash);
}
