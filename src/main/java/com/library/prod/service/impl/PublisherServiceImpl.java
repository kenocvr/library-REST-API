package com.library.prod.service.impl;

import com.library.prod.service.PublisherService;
import com.library.prod.domain.Publisher;
import com.library.prod.repository.PublisherRepository;
import com.library.prod.repository.search.PublisherSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Publisher}.
 */
@Service
@Transactional
public class PublisherServiceImpl implements PublisherService {

    private final Logger log = LoggerFactory.getLogger(PublisherServiceImpl.class);

    private final PublisherRepository publisherRepository;

    private final PublisherSearchRepository publisherSearchRepository;

    public PublisherServiceImpl(PublisherRepository publisherRepository, PublisherSearchRepository publisherSearchRepository) {
        this.publisherRepository = publisherRepository;
        this.publisherSearchRepository = publisherSearchRepository;
    }

    /**
     * Save a publisher.
     *
     * @param publisher the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Publisher save(Publisher publisher) {
        log.debug("Request to save Publisher : {}", publisher);
        Publisher result = publisherRepository.save(publisher);
        publisherSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the publishers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Publisher> findAll(Pageable pageable) {
        log.debug("Request to get all Publishers");
        return publisherRepository.findAll(pageable);
    }


    /**
     * Get one publisher by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Publisher> findOne(Long id) {
        log.debug("Request to get Publisher : {}", id);
        return publisherRepository.findById(id);
    }

    /**
     * Delete the publisher by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Publisher : {}", id);
        publisherRepository.deleteById(id);
        publisherSearchRepository.deleteById(id);
    }

    /**
     * Search for the publisher corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Publisher> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Publishers for query {}", query);
        return publisherSearchRepository.search(queryStringQuery(query), pageable);    }
}
