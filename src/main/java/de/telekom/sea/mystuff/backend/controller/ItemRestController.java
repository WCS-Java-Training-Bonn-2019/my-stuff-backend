package de.telekom.sea.mystuff.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.telekom.sea.mystuff.backend.model.Item;
import de.telekom.sea.mystuff.backend.repository.ItemRepository;

@RestController
@RequestMapping("/api/v1/")
public class ItemRestController {

	private final ItemRepository repository;

	@Autowired
	public ItemRestController(ItemRepository repository) {
		this.repository = repository;
	}

	@GetMapping("items")	
	public List<Item> getAll() {
		return repository.findAll();
	}
	
	@GetMapping("item/{id}")	
	public Item get(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
	}

	@PostMapping("items")
	@ResponseStatus(HttpStatus.CREATED)
	public Item newItem(@RequestBody Item newItem) {
		newItem.setId(null);
		return repository.save(newItem);
	}
	
	@PutMapping("item/{id}")
	public Item replace(@RequestBody Item newItem, @PathVariable Long id) {
		return repository.findById(id).map(item -> {
			item.setAmount(newItem.getAmount());
			item.setDescription(newItem.getDescription());
			item.setLastUsed(newItem.getLastUsed());
			item.setLocation(newItem.getLocation());
			item.setName(newItem.getName());
			item.setPhoto(newItem.getPhoto());
			return repository.save(item);
		}).orElseThrow(() -> new ResourceNotFoundException(id));
	}

	@DeleteMapping("item/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(id);
		}
	}
}
