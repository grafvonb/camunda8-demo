package org.dzbank.zielbildbpm.c8demo.microservices.one;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class OneEntityControllerIT {

    @Autowired
    private WebTestClient client;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<UUID> retrieveIds() {
        return jdbcTemplate.queryForList("SELECT id FROM zielbildbpm_one_entity", UUID.class);
    }

    private OneEntity retrieveOne(UUID id) {
        return jdbcTemplate.queryForObject("SELECT * FROM zielbildbpm_one_entity WHERE id = ?",
                (rs, rowNum) -> new OneEntity(UUID.fromString(rs.getString("id")),
                        rs.getString("correlation_id"),
                        rs.getString("body_one")), id);
    }

    @Test
    void shouldRetrieveAll() {
        List<UUID> ids = retrieveIds();
        client.get()
                .uri("/entities")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OneEntity.class).hasSize(3)
                .consumeWith(System.out::println);
    }

    @ParameterizedTest(name = "Entity id: {0}")
    @MethodSource("retrieveIds")
    void shouldRetrieveSingleIfExist(UUID id) {
        client.get()
                .uri("/entities/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id.toString());
    }

    @Test
    void shouldCreateNewEntity() {
        OneEntity entity = new OneEntity("test correlation", "test body one");
        client.post()
                .uri("/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(entity), OneEntity.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.correlationId").isEqualTo("test correlation")
                .jsonPath("$.bodyOne").isEqualTo("test body one");
    }

    @Test
    void shouldNotCreateNewEntityIfDoesNotContainExpectedTextInBody() {
        OneEntity entity = new OneEntity("test correlation", "test without expected phrase");
        client.post()
                .uri("/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(entity), OneEntity.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldUpdateEntity() {
        List<UUID> ids = retrieveIds();
        assertFalse(ids.isEmpty());

        OneEntity entity = retrieveOne(ids.get(0));
        entity.setBodyOne(entity.getBodyOne().concat(" (updated)"));

        client.put()
                .uri("/entities/{id}", entity.getId())
                .body(Mono.just(entity), OneEntity.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OneEntity.class)
                .consumeWith(System.out::println);
    }

    @Test
    void shouldDeleteEntity() {
        List<UUID> ids = retrieveIds();
        assertFalse(ids.isEmpty());

        client.get()
                .uri("/entities/{id}", ids.get(0))
                .exchange()
                .expectStatus().isOk();

        client.delete()
                .uri("/entities/{id}", ids.get(0))
                .exchange()
                .expectStatus().isNoContent();

        client.get()
                .uri("/entities/{id}", ids.get(0))
                .exchange()
                .expectStatus().isNotFound();
    }
}