package com.library.prod.service;

import com.library.prod.domain.Client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Client}.
 */
public interface ClientService {

    /**
     * Save a client.
     *
     * @param client the entity to save.
     * @return the persisted entity.
     */
    Client save(Client client);

    /**
     * Get all the clients.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Client> findAll(Pageable pageable);


    /**
     * Get the "id" client.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Client> findOne(Long id);

    /**
     * Delete the "id" client.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the client corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Client> search(String query, Pageable pageable);
}
