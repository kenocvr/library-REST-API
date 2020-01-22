package com.library.prod.web.rest;

import com.library.prod.domain.BorrowedBook;
import com.library.prod.service.BorrowedBookService;
import com.library.prod.web.rest.errors.BadRequestAlertException;
import com.library.prod.service.dto.BorrowedBookCriteria;
import com.library.prod.service.BorrowedBookQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.library.prod.domain.BorrowedBook}.
 */
@RestController
@RequestMapping("/api")
public class BorrowedBookResource {

    private final Logger log = LoggerFactory.getLogger(BorrowedBookResource.class);

    private static final String ENTITY_NAME = "borrowedBook";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BorrowedBookService borrowedBookService;

    private final BorrowedBookQueryService borrowedBookQueryService;

    public BorrowedBookResource(BorrowedBookService borrowedBookService, BorrowedBookQueryService borrowedBookQueryService) {
        this.borrowedBookService = borrowedBookService;
        this.borrowedBookQueryService = borrowedBookQueryService;
    }

    /**
     * {@code POST  /borrowed-books} : Create a new borrowedBook.
     *
     * @param borrowedBook the borrowedBook to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new borrowedBook, or with status {@code 400 (Bad Request)} if the borrowedBook has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/borrowed-books")
    public ResponseEntity<BorrowedBook> createBorrowedBook(@RequestBody BorrowedBook borrowedBook) throws URISyntaxException {
        log.debug("REST request to save BorrowedBook : {}", borrowedBook);
        if (borrowedBook.getId() != null) {
            throw new BadRequestAlertException("A new borrowedBook cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BorrowedBook result = borrowedBookService.save(borrowedBook);
        return ResponseEntity.created(new URI("/api/borrowed-books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /borrowed-books} : Updates an existing borrowedBook.
     *
     * @param borrowedBook the borrowedBook to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated borrowedBook,
     * or with status {@code 400 (Bad Request)} if the borrowedBook is not valid,
     * or with status {@code 500 (Internal Server Error)} if the borrowedBook couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/borrowed-books")
    public ResponseEntity<BorrowedBook> updateBorrowedBook(@RequestBody BorrowedBook borrowedBook) throws URISyntaxException {
        log.debug("REST request to update BorrowedBook : {}", borrowedBook);
        if (borrowedBook.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BorrowedBook result = borrowedBookService.save(borrowedBook);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, borrowedBook.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /borrowed-books} : get all the borrowedBooks.
     *

     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of borrowedBooks in body.
     */
    @GetMapping("/borrowed-books")
    public ResponseEntity<List<BorrowedBook>> getAllBorrowedBooks(BorrowedBookCriteria criteria, Pageable pageable) {
        log.debug("REST request to get BorrowedBooks by criteria: {}", criteria);
        Page<BorrowedBook> page = borrowedBookQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * {@code GET  /borrowed-books/count} : count all the borrowedBooks.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
    */
    @GetMapping("/borrowed-books/count")
    public ResponseEntity<Long> countBorrowedBooks(BorrowedBookCriteria criteria) {
        log.debug("REST request to count BorrowedBooks by criteria: {}", criteria);
        return ResponseEntity.ok().body(borrowedBookQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /borrowed-books/:id} : get the "id" borrowedBook.
     *
     * @param id the id of the borrowedBook to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the borrowedBook, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/borrowed-books/{id}")
    public ResponseEntity<BorrowedBook> getBorrowedBook(@PathVariable Long id) {
        log.debug("REST request to get BorrowedBook : {}", id);
        Optional<BorrowedBook> borrowedBook = borrowedBookService.findOne(id);
        return ResponseUtil.wrapOrNotFound(borrowedBook);
    }

    /**
     * {@code DELETE  /borrowed-books/:id} : delete the "id" borrowedBook.
     *
     * @param id the id of the borrowedBook to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/borrowed-books/{id}")
    public ResponseEntity<Void> deleteBorrowedBook(@PathVariable Long id) {
        log.debug("REST request to delete BorrowedBook : {}", id);
        borrowedBookService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/borrowed-books?query=:query} : search for the borrowedBook corresponding
     * to the query.
     *
     * @param query the query of the borrowedBook search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/borrowed-books")
    public ResponseEntity<List<BorrowedBook>> searchBorrowedBooks(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of BorrowedBooks for query {}", query);
        Page<BorrowedBook> page = borrowedBookService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
