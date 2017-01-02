package com.itranswarp.bitcoin.explorer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BlockController {

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
