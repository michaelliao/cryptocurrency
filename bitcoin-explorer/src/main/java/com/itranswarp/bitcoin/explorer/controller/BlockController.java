package com.itranswarp.bitcoin.explorer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.itranswarp.bitcoin.explorer.service.BitcoinService;

@Controller
public class BlockController {

	@Autowired
	BitcoinService bitcoinService;

	@GetMapping("/")
	public ModelAndView viewIndex() {
		return new ModelAndView("index");
	}

	@GetMapping("/block/{hash}")
	public ModelAndView viewBlock(@PathVariable("hash") String hash) {
		return new ModelAndView("block");
	}

	@GetMapping("/tx/{hash}")
	public ModelAndView viewTx(@PathVariable("hash") String hash) {
		return null;
	}
}
