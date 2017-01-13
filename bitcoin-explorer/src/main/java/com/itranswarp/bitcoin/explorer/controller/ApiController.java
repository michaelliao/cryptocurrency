package com.itranswarp.bitcoin.explorer.controller;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.itranswarp.bitcoin.explorer.domain.BlockEntity;
import com.itranswarp.bitcoin.explorer.domain.TxEntity;
import com.itranswarp.bitcoin.explorer.exception.ApiNotFoundException;
import com.itranswarp.bitcoin.explorer.repository.BlockRepository;
import com.itranswarp.bitcoin.explorer.repository.TxRepository;
import com.itranswarp.bitcoin.explorer.service.BitcoinService;

@RestController
public class ApiController {

	static final Pattern PATTERN_SHA256 = Pattern.compile("^[0-9a-f]{64}$");
	static final Pattern PATTERN_INT = Pattern.compile("^[0-9]{1,8}$");

	@Autowired
	BitcoinService bitcoinService;

	@Autowired
	BlockRepository blockRepository;

	@Autowired
	TxRepository txRepository;

	@GetMapping("/api/block/{hash}")
	public Object getBlock(@PathVariable("hash") String input) {
		BlockEntity block = null;
		if (PATTERN_SHA256.matcher(input).matches()) {
			block = blockRepository.findOne(input);
		} else if (PATTERN_INT.matcher(input).matches()) {
			block = blockRepository.findOneByHeight(Long.parseLong(input));
		}
		if (block == null) {
			throw new ApiNotFoundException("Block not found");
		}
		return block;
	}

	@GetMapping("/api/tx/{hash}")
	public Object getTx(@PathVariable("hash") String input) {
		TxEntity tx = null;
		if (PATTERN_SHA256.matcher(input).matches()) {
			tx = txRepository.findOne(input);
		}
		if (tx == null) {
			throw new ApiNotFoundException("Transaction not found");
		}
		return tx;
	}

	/**
	 * API sample:
	 * 
	 * https://blockchain.info/address/1EzwoHtiXB4iFwedPr49iywjZn2nnekhoj?format=json
	 */
	@GetMapping("/api/address/{address}")
	public Object address(@PathVariable("address") String address) {
		return null;
	}
}
