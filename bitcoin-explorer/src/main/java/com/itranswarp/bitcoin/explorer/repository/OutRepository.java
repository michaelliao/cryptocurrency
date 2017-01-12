package com.itranswarp.bitcoin.explorer.repository;

import org.springframework.data.repository.CrudRepository;

import com.itranswarp.bitcoin.explorer.domain.OutEntity;

public interface OutRepository extends CrudRepository<OutEntity, String> {

}
