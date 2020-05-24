package de.telekom.sea.mystuff.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import de.telekom.sea.mystuff.backend.model.Item;
import de.telekom.sea.mystuff.backend.repository.ItemRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ItemRestControllerTest {

	private static final String BASE_PATH = "/api/v1/";

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ItemRepository repo;

	@BeforeEach
	void setupRepo() {
		repo.deleteAll();
	}

	@Test
	void shouldBeAbleToUploadAnItem() throws URISyntaxException {
		// Given | Arrange
		ResponseEntity<Item> response = givenAnInsertedItem();
		// When | Act
		// Then | Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody().getId()).isNotNull();
	}
	
	@Test
	void shouldReadAllItems() throws URISyntaxException {
		// Given | Arrange
		Item lawnMower = givenAnInsertedItem().getBody();
		// When | Act
		ResponseEntity<Item[]> response = restTemplate.getForEntity(BASE_PATH + "items", Item[].class);
		// Then | Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).hasSize(1);
		assertThat(response.getBody()[0]).isEqualToComparingFieldByField(lawnMower);
	}

	@Test
	void shouldFindOneItem() throws URISyntaxException {
		// Given | Arrange
		Item lawnMower = givenAnInsertedItem().getBody();
		// When | Act
		ResponseEntity<Item> response = restTemplate.getForEntity(BASE_PATH + "item/" + lawnMower.getId(), Item.class);
		// Then | Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualToComparingFieldByField(lawnMower);
	}

	@Test
	void shouldFindNoItemForUnknownId() throws URISyntaxException {
		// Given | Arrange
		// When | Act
		ResponseEntity<Item> response = restTemplate.getForEntity(BASE_PATH + "item/1", Item.class);
		// Then | Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldBeAbleToDeleteAnItem() throws URISyntaxException {
		// Given | Arrange
		Item lawnMower = givenAnInsertedItem().getBody();
		// When | Act
		RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.DELETE,
				new URI(restTemplate.getRootUri() + BASE_PATH + "item/" + lawnMower.getId()));
		ResponseEntity<String> deleteResponse = restTemplate.exchange(requestEntity, String.class);
		// Then | Assert
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		ResponseEntity<Item> getResponse = restTemplate.getForEntity(BASE_PATH + "item/" + lawnMower.getId(),
				Item.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotBeAbleToDeleteAnItemWithUnknownId() throws URISyntaxException {
		// Given | Arrange
		// When | Act
		RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.DELETE,
				new URI(restTemplate.getRootUri() + BASE_PATH + "/1"));
		ResponseEntity<String> deleteResponse = restTemplate.exchange(requestEntity, String.class);
		// Then | Assert
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldBeAbleToReplaceAnItem() throws URISyntaxException {
		// Given | Arrange
		Item lawnMower = givenAnInsertedItem().getBody();
		Item lawnTrimmer = buildLawnTrimmer();
		// When | Act
		RequestEntity<Item> requestEntity = new RequestEntity<>(lawnTrimmer, HttpMethod.PUT,
				new URI(restTemplate.getRootUri() + BASE_PATH + "item/" + lawnMower.getId()));
		ResponseEntity<String> putResponse = restTemplate.exchange(requestEntity, String.class);
		// Then | Assert
		assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		ResponseEntity<Item> getResponse = restTemplate.getForEntity(BASE_PATH + "item/" + lawnMower.getId(),
				Item.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody()).isEqualToComparingOnlyGivenFields(lawnTrimmer, "name");
	}

	@Test
	void shouldNotBeAbleToReplaceAnItemWithUnknownId() throws URISyntaxException {
		// Given | Arrange
		Item lawnTrimmer = buildLawnTrimmer();
		// When | Act
		RequestEntity<Item> requestEntity = new RequestEntity<>(lawnTrimmer, HttpMethod.PUT,
				new URI(restTemplate.getRootUri() + BASE_PATH + "item/1"));
		ResponseEntity<String> putResponse = restTemplate.exchange(requestEntity, String.class);
		// Then | Assert
		assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	private ResponseEntity<Item> givenAnInsertedItem() {
		Item item = buildLawnMower();
		return restTemplate.postForEntity(BASE_PATH + "/items", item, Item.class);
	}

	private Item buildLawnMower() {
		Item item = Item.builder().name("Lawn mower").amount(1).lastUsed(Date.valueOf("2019-05-01"))
				.location("Basement").build();
		return item;
	}

	private Item buildLawnTrimmer() {
		Item item = Item.builder().name("Lawn trimmer").amount(1).lastUsed(Date.valueOf("2018-05-01"))
				.location("Basement").build();
		return item;
	}

}
