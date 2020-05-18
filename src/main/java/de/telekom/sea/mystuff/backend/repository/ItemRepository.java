package de.telekom.sea.mystuff.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.telekom.sea.mystuff.backend.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long>{

}
