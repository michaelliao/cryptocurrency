package com.itranswarp.bitcoin.explorer.repository;

import org.springframework.data.repository.CrudRepository;

import com.itranswarp.bitcoin.explorer.domain.OutputEntity;

public interface OutputRepository extends CrudRepository<OutputEntity, String> {

}
