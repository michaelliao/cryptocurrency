package com.itranswarp.bitcoin.explorer.repository;

import org.springframework.data.repository.CrudRepository;

import com.itranswarp.bitcoin.explorer.domain.TxEntity;

public interface TxRepository extends CrudRepository<TxEntity, String> {

}
