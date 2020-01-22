package com.library.prod.service.impl;

import com.library.prod.service.BorrowedBookService;
import com.library.prod.domain.BorrowedBook;
import com.library.prod.repository.BorrowedBookRepository;
import com.library.prod.repository.search.BorrowedBookSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link BorrowedBook}.
 */
@Service
@Transactional
public class BorrowedBookServiceImpl implements BorrowedBookService {

    private final Logger log = LoggerFactory.getLogger(BorrowedBookServiceImpl.class);

    private final BorrowedBookRepository borrowedBookRepository;

    private final BorrowedBookSearchRepository borrowedBookSearchRepository;

    public BorrowedBookServiceImpl(BorrowedBookRepository borrowedBookRepository, BorrowedBookSearchRepository borrowedBookSearchRepository) {
        this.borrowedBookRepository = borrowedBookRepository;
        this.borrowedBookSearchRepository = borrowedBookSearchRepository;
    }

    /**
     * Save a borrowedBook.
     *
     * @param borrowedBook the entity to save.
     * @return the persisted entity.
     */
    @Override
    public BorrowedBook save(BorrowedBook borrowedBook) {
        log.debug("Request to save BorrowedBook : {}", borrowedBook);
        BorrowedBook result = borrowedBookRepository.save(borrowedBook);
        borrowedBookSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the borrowedBooks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BorrowedBook> findAll(Pageable pageable) {
        log.debug("Request to get all BorrowedBooks");
        return borrowedBookRepository.findAll(pageable);
    }


    /**
     * Get one borrowedBook by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<BorrowedBook> findOne(Long id) {
        log.debug("Request to get BorrowedBook : {}", id);
        return borrowedBookRepository.findById(id);
    }

    /**
     * Delete the borrowedBook by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete BorrowedBook : {}", id);
        borrowedBookRepository.deleteById(id);
        borrowedBookSearchRepository.deleteById(id);
    }

    /**
     * Search for the borrowedBook corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BorrowedBook> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BorrowedBooks for query {}", query);
        return borrowedBookSearchRepository.search(queryStringQuery(query), pageable);    }
}
