package de.telekom.sea.mystuff.backend.bootstrap;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import de.telekom.sea.mystuff.backend.model.Item;
import de.telekom.sea.mystuff.backend.repository.ItemRepository;

@Component
@ConditionalOnProperty(value = "app.init.data", havingValue = "true", matchIfMissing = false)
public class DevBootstrap implements ApplicationListener<ContextRefreshedEvent> {

	private final ItemRepository repository;

	@Autowired
	public DevBootstrap(ItemRepository repository) {
		this.repository = repository;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		repository.save(Item.builder().name("Lawn mower").description("My good old lawnmower").amount(1).lastUsed(Date.valueOf("2019-05-01")).location("Basement").build());
		repository.save(Item.builder().name("DVD player").description("from 1988").amount(1).lastUsed(Date.valueOf("2014-12-31")).location("Basement").build());
		repository.save(Item.builder().name("Car tires (winter)").description("no alu").amount(4).lastUsed(Date.valueOf("2010-03-01")).location("Garage").build());

	}

}
