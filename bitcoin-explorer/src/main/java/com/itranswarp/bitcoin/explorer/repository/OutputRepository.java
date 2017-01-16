package com.itranswarp.bitcoin.explorer.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.itranswarp.bitcoin.explorer.domain.OutputEntity;

public interface OutputRepository extends CrudRepository<OutputEntity, String> {

	List<OutputEntity> findByTxoutHashOrderByTxoutIndex(String txHash);

	List<OutputEntity> findByTxinHashOrderByTxinIndex(String txHash);
}
