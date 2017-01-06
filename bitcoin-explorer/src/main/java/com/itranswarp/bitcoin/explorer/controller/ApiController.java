package com.itranswarp.bitcoin.explorer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.itranswarp.bitcoin.explorer.service.BitcoinService;

@RestController
public class ApiController {

	@Autowired
	BitcoinService bitcoinService;

	@GetMapping("/api/block/{hash}")
	public Object getBlock(@PathVariable("hash") String hash) {
		return null;
	}

	@GetMapping("/api/block/{hash}/header")
	public Object getHeader(@PathVariable("hash") String hash) {
		return null;
	}

	@GetMapping("/api/tx/{hash}")
	public Object getTx(@PathVariable("hash") String hash) {
		return null;
	}

}
