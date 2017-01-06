package com.itranswarp.bitcoin.explorer.repository;

import org.springframework.data.repository.CrudRepository;

import com.itranswarp.bitcoin.explorer.domain.TxEntity;

public interface TxEntityRepository extends CrudRepository<TxEntity, String> {

}
