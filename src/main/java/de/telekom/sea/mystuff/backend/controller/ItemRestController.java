package de.telekom.sea.mystuff.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemRestController {

	@GetMapping("/api/v1/items")
	public String listItems() {
		return "items";
	}
}
