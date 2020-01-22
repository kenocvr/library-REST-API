package com.library.prod.web.rest;

import com.library.prod.LibraryApp;
import com.library.prod.domain.Book;
import com.library.prod.domain.Publisher;
import com.library.prod.domain.Author;
import com.library.prod.repository.BookRepository;
import com.library.prod.repository.search.BookSearchRepository;
import com.library.prod.service.BookService;
import com.library.prod.web.rest.errors.ExceptionTranslator;
import com.library.prod.service.dto.BookCriteria;
import com.library.prod.service.BookQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.library.prod.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BookResource} REST controller.
 */
@SpringBootTest(classes = LibraryApp.class)
public class BookResourceIT {

    private static final String DEFAULT_ISBN = "AAAAAAAAAA";
    private static final String UPDATED_ISBN = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLISH_YEAR = "AAAAAAAAAA";
    private static final String UPDATED_PUBLISH_YEAR = "BBBBBBBBBB";

    private static final Integer DEFAULT_COPIES = 1;
    private static final Integer UPDATED_COPIES = 2;
    private static final Integer SMALLER_COPIES = 1 - 1;

    private static final byte[] DEFAULT_COVER = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_COVER = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_COVER_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_COVER_CONTENT_TYPE = "image/png";

    @Autowired
    private BookRepository bookRepository;

    @Mock
    private BookRepository bookRepositoryMock;

    @Mock
    private BookService bookServiceMock;

    @Autowired
    private BookService bookService;

    /**
     * This repository is mocked in the com.library.prod.repository.search test package.
     *
     * @see com.library.prod.repository.search.BookSearchRepositoryMockConfiguration
     */
    @Autowired
    private BookSearchRepository mockBookSearchRepository;

    @Autowired
    private BookQueryService bookQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restBookMockMvc;

    private Book book;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BookResource bookResource = new BookResource(bookService, bookQueryService);
        this.restBookMockMvc = MockMvcBuilders.standaloneSetup(bookResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createEntity(EntityManager em) {
        Book book = new Book()
            .isbn(DEFAULT_ISBN)
            .name(DEFAULT_NAME)
            .publishYear(DEFAULT_PUBLISH_YEAR)
            .copies(DEFAULT_COPIES)
            .cover(DEFAULT_COVER)
            .coverContentType(DEFAULT_COVER_CONTENT_TYPE);
        return book;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createUpdatedEntity(EntityManager em) {
        Book book = new Book()
            .isbn(UPDATED_ISBN)
            .name(UPDATED_NAME)
            .publishYear(UPDATED_PUBLISH_YEAR)
            .copies(UPDATED_COPIES)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE);
        return book;
    }

    @BeforeEach
    public void initTest() {
        book = createEntity(em);
    }

    @Test
    @Transactional
    public void createBook() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().size();

        // Create the Book
        restBookMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isCreated());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getIsbn()).isEqualTo(DEFAULT_ISBN);
        assertThat(testBook.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBook.getPublishYear()).isEqualTo(DEFAULT_PUBLISH_YEAR);
        assertThat(testBook.getCopies()).isEqualTo(DEFAULT_COPIES);
        assertThat(testBook.getCover()).isEqualTo(DEFAULT_COVER);
        assertThat(testBook.getCoverContentType()).isEqualTo(DEFAULT_COVER_CONTENT_TYPE);

        // Validate the Book in Elasticsearch
        verify(mockBookSearchRepository, times(1)).save(testBook);
    }

    @Test
    @Transactional
    public void createBookWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().size();

        // Create the Book with an existing ID
        book.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate);

        // Validate the Book in Elasticsearch
        verify(mockBookSearchRepository, times(0)).save(book);
    }


    @Test
    @Transactional
    public void checkIsbnIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setIsbn(null);

        // Create the Book, which fails.

        restBookMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setName(null);

        // Create the Book, which fails.

        restBookMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPublishYearIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setPublishYear(null);

        // Create the Book, which fails.

        restBookMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCopiesIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setCopies(null);

        // Create the Book, which fails.

        restBookMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBooks() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList
        restBookMockMvc.perform(get("/api/books?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].publishYear").value(hasItem(DEFAULT_PUBLISH_YEAR)))
            .andExpect(jsonPath("$.[*].copies").value(hasItem(DEFAULT_COPIES)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64Utils.encodeToString(DEFAULT_COVER))));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllBooksWithEagerRelationshipsIsEnabled() throws Exception {
        BookResource bookResource = new BookResource(bookServiceMock, bookQueryService);
        when(bookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restBookMockMvc = MockMvcBuilders.standaloneSetup(bookResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restBookMockMvc.perform(get("/api/books?eagerload=true"))
        .andExpect(status().isOk());

        verify(bookServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllBooksWithEagerRelationshipsIsNotEnabled() throws Exception {
        BookResource bookResource = new BookResource(bookServiceMock, bookQueryService);
            when(bookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restBookMockMvc = MockMvcBuilders.standaloneSetup(bookResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restBookMockMvc.perform(get("/api/books?eagerload=true"))
        .andExpect(status().isOk());

            verify(bookServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get the book
        restBookMockMvc.perform(get("/api/books/{id}", book.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(book.getId().intValue()))
            .andExpect(jsonPath("$.isbn").value(DEFAULT_ISBN))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.publishYear").value(DEFAULT_PUBLISH_YEAR))
            .andExpect(jsonPath("$.copies").value(DEFAULT_COPIES))
            .andExpect(jsonPath("$.coverContentType").value(DEFAULT_COVER_CONTENT_TYPE))
            .andExpect(jsonPath("$.cover").value(Base64Utils.encodeToString(DEFAULT_COVER)));
    }


    @Test
    @Transactional
    public void getBooksByIdFiltering() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        Long id = book.getId();

        defaultBookShouldBeFound("id.equals=" + id);
        defaultBookShouldNotBeFound("id.notEquals=" + id);

        defaultBookShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBookShouldNotBeFound("id.greaterThan=" + id);

        defaultBookShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBookShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBooksByIsbnIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn equals to DEFAULT_ISBN
        defaultBookShouldBeFound("isbn.equals=" + DEFAULT_ISBN);

        // Get all the bookList where isbn equals to UPDATED_ISBN
        defaultBookShouldNotBeFound("isbn.equals=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    public void getAllBooksByIsbnIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn not equals to DEFAULT_ISBN
        defaultBookShouldNotBeFound("isbn.notEquals=" + DEFAULT_ISBN);

        // Get all the bookList where isbn not equals to UPDATED_ISBN
        defaultBookShouldBeFound("isbn.notEquals=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    public void getAllBooksByIsbnIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn in DEFAULT_ISBN or UPDATED_ISBN
        defaultBookShouldBeFound("isbn.in=" + DEFAULT_ISBN + "," + UPDATED_ISBN);

        // Get all the bookList where isbn equals to UPDATED_ISBN
        defaultBookShouldNotBeFound("isbn.in=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    public void getAllBooksByIsbnIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn is not null
        defaultBookShouldBeFound("isbn.specified=true");

        // Get all the bookList where isbn is null
        defaultBookShouldNotBeFound("isbn.specified=false");
    }
                @Test
    @Transactional
    public void getAllBooksByIsbnContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn contains DEFAULT_ISBN
        defaultBookShouldBeFound("isbn.contains=" + DEFAULT_ISBN);

        // Get all the bookList where isbn contains UPDATED_ISBN
        defaultBookShouldNotBeFound("isbn.contains=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    public void getAllBooksByIsbnNotContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn does not contain DEFAULT_ISBN
        defaultBookShouldNotBeFound("isbn.doesNotContain=" + DEFAULT_ISBN);

        // Get all the bookList where isbn does not contain UPDATED_ISBN
        defaultBookShouldBeFound("isbn.doesNotContain=" + UPDATED_ISBN);
    }


    @Test
    @Transactional
    public void getAllBooksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where name equals to DEFAULT_NAME
        defaultBookShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the bookList where name equals to UPDATED_NAME
        defaultBookShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBooksByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where name not equals to DEFAULT_NAME
        defaultBookShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the bookList where name not equals to UPDATED_NAME
        defaultBookShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBooksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBookShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the bookList where name equals to UPDATED_NAME
        defaultBookShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBooksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where name is not null
        defaultBookShouldBeFound("name.specified=true");

        // Get all the bookList where name is null
        defaultBookShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllBooksByNameContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where name contains DEFAULT_NAME
        defaultBookShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the bookList where name contains UPDATED_NAME
        defaultBookShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBooksByNameNotContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where name does not contain DEFAULT_NAME
        defaultBookShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the bookList where name does not contain UPDATED_NAME
        defaultBookShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllBooksByPublishYearIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear equals to DEFAULT_PUBLISH_YEAR
        defaultBookShouldBeFound("publishYear.equals=" + DEFAULT_PUBLISH_YEAR);

        // Get all the bookList where publishYear equals to UPDATED_PUBLISH_YEAR
        defaultBookShouldNotBeFound("publishYear.equals=" + UPDATED_PUBLISH_YEAR);
    }

    @Test
    @Transactional
    public void getAllBooksByPublishYearIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear not equals to DEFAULT_PUBLISH_YEAR
        defaultBookShouldNotBeFound("publishYear.notEquals=" + DEFAULT_PUBLISH_YEAR);

        // Get all the bookList where publishYear not equals to UPDATED_PUBLISH_YEAR
        defaultBookShouldBeFound("publishYear.notEquals=" + UPDATED_PUBLISH_YEAR);
    }

    @Test
    @Transactional
    public void getAllBooksByPublishYearIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear in DEFAULT_PUBLISH_YEAR or UPDATED_PUBLISH_YEAR
        defaultBookShouldBeFound("publishYear.in=" + DEFAULT_PUBLISH_YEAR + "," + UPDATED_PUBLISH_YEAR);

        // Get all the bookList where publishYear equals to UPDATED_PUBLISH_YEAR
        defaultBookShouldNotBeFound("publishYear.in=" + UPDATED_PUBLISH_YEAR);
    }

    @Test
    @Transactional
    public void getAllBooksByPublishYearIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear is not null
        defaultBookShouldBeFound("publishYear.specified=true");

        // Get all the bookList where publishYear is null
        defaultBookShouldNotBeFound("publishYear.specified=false");
    }
                @Test
    @Transactional
    public void getAllBooksByPublishYearContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear contains DEFAULT_PUBLISH_YEAR
        defaultBookShouldBeFound("publishYear.contains=" + DEFAULT_PUBLISH_YEAR);

        // Get all the bookList where publishYear contains UPDATED_PUBLISH_YEAR
        defaultBookShouldNotBeFound("publishYear.contains=" + UPDATED_PUBLISH_YEAR);
    }

    @Test
    @Transactional
    public void getAllBooksByPublishYearNotContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear does not contain DEFAULT_PUBLISH_YEAR
        defaultBookShouldNotBeFound("publishYear.doesNotContain=" + DEFAULT_PUBLISH_YEAR);

        // Get all the bookList where publishYear does not contain UPDATED_PUBLISH_YEAR
        defaultBookShouldBeFound("publishYear.doesNotContain=" + UPDATED_PUBLISH_YEAR);
    }


    @Test
    @Transactional
    public void getAllBooksByCopiesIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where copies equals to DEFAULT_COPIES
        defaultBookShouldBeFound("copies.equals=" + DEFAULT_COPIES);

        // Get all the bookList where copies equals to UPDATED_COPIES
        defaultBookShouldNotBeFound("copies.equals=" + UPDATED_COPIES);
    }

    @Test
    @Transactional
    public void getAllBooksByCopiesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where copies not equals to DEFAULT_COPIES
        defaultBookShouldNotBeFound("copies.notEquals=" + DEFAULT_COPIES);

        // Get all the bookList where copies not equals to UPDATED_COPIES
        defaultBookShouldBeFound("copies.notEquals=" + UPDATED_COPIES);
    }

    @Test
    @Transactional
    public void getAllBooksByCopiesIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where copies in DEFAULT_COPIES or UPDATED_COPIES
        defaultBookShouldBeFound("copies.in=" + DEFAULT_COPIES + "," + UPDATED_COPIES);

        // Get all the bookList where copies equals to UPDATED_COPIES
        defaultBookShouldNotBeFound("copies.in=" + UPDATED_COPIES);
    }

    @Test
    @Transactional
    public void getAllBooksByCopiesIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where copies is not null
        defaultBookShouldBeFound("copies.specified=true");

        // Get all the bookList where copies is null
        defaultBookShouldNotBeFound("copies.specified=false");
    }

    @Test
    @Transactional
    public void getAllBooksByCopiesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where copies is greater than or equal to DEFAULT_COPIES
        defaultBookShouldBeFound("copies.greaterThanOrEqual=" + DEFAULT_COPIES);

        // Get all the bookList where copies is greater than or equal to UPDATED_COPIES
        defaultBookShouldNotBeFound("copies.greaterThanOrEqual=" + UPDATED_COPIES);
    }

    @Test
    @Transactional
    public void getAllBooksByCopiesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where copies is less than or equal to DEFAULT_COPIES
        defaultBookShouldBeFound("copies.lessThanOrEqual=" + DEFAULT_COPIES);

        // Get all the bookList where copies is less than or equal to SMALLER_COPIES
        defaultBookShouldNotBeFound("copies.lessThanOrEqual=" + SMALLER_COPIES);
    }

    @Test
    @Transactional
    public void getAllBooksByCopiesIsLessThanSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where copies is less than DEFAULT_COPIES
        defaultBookShouldNotBeFound("copies.lessThan=" + DEFAULT_COPIES);

        // Get all the bookList where copies is less than UPDATED_COPIES
        defaultBookShouldBeFound("copies.lessThan=" + UPDATED_COPIES);
    }

    @Test
    @Transactional
    public void getAllBooksByCopiesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where copies is greater than DEFAULT_COPIES
        defaultBookShouldNotBeFound("copies.greaterThan=" + DEFAULT_COPIES);

        // Get all the bookList where copies is greater than SMALLER_COPIES
        defaultBookShouldBeFound("copies.greaterThan=" + SMALLER_COPIES);
    }


    @Test
    @Transactional
    public void getAllBooksByPublisherIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);
        Publisher publisher = PublisherResourceIT.createEntity(em);
        em.persist(publisher);
        em.flush();
        book.setPublisher(publisher);
        bookRepository.saveAndFlush(book);
        Long publisherId = publisher.getId();

        // Get all the bookList where publisher equals to publisherId
        defaultBookShouldBeFound("publisherId.equals=" + publisherId);

        // Get all the bookList where publisher equals to publisherId + 1
        defaultBookShouldNotBeFound("publisherId.equals=" + (publisherId + 1));
    }


    @Test
    @Transactional
    public void getAllBooksByAuthorIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);
        Author author = AuthorResourceIT.createEntity(em);
        em.persist(author);
        em.flush();
        book.addAuthor(author);
        bookRepository.saveAndFlush(book);
        Long authorId = author.getId();

        // Get all the bookList where author equals to authorId
        defaultBookShouldBeFound("authorId.equals=" + authorId);

        // Get all the bookList where author equals to authorId + 1
        defaultBookShouldNotBeFound("authorId.equals=" + (authorId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookShouldBeFound(String filter) throws Exception {
        restBookMockMvc.perform(get("/api/books?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].publishYear").value(hasItem(DEFAULT_PUBLISH_YEAR)))
            .andExpect(jsonPath("$.[*].copies").value(hasItem(DEFAULT_COPIES)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64Utils.encodeToString(DEFAULT_COVER))));

        // Check, that the count call also returns 1
        restBookMockMvc.perform(get("/api/books/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookShouldNotBeFound(String filter) throws Exception {
        restBookMockMvc.perform(get("/api/books?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookMockMvc.perform(get("/api/books/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingBook() throws Exception {
        // Get the book
        restBookMockMvc.perform(get("/api/books/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBook() throws Exception {
        // Initialize the database
        bookService.save(book);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockBookSearchRepository);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book
        Book updatedBook = bookRepository.findById(book.getId()).get();
        // Disconnect from session so that the updates on updatedBook are not directly saved in db
        em.detach(updatedBook);
        updatedBook
            .isbn(UPDATED_ISBN)
            .name(UPDATED_NAME)
            .publishYear(UPDATED_PUBLISH_YEAR)
            .copies(UPDATED_COPIES)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE);

        restBookMockMvc.perform(put("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBook)))
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getIsbn()).isEqualTo(UPDATED_ISBN);
        assertThat(testBook.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBook.getPublishYear()).isEqualTo(UPDATED_PUBLISH_YEAR);
        assertThat(testBook.getCopies()).isEqualTo(UPDATED_COPIES);
        assertThat(testBook.getCover()).isEqualTo(UPDATED_COVER);
        assertThat(testBook.getCoverContentType()).isEqualTo(UPDATED_COVER_CONTENT_TYPE);

        // Validate the Book in Elasticsearch
        verify(mockBookSearchRepository, times(1)).save(testBook);
    }

    @Test
    @Transactional
    public void updateNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Create the Book

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc.perform(put("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Book in Elasticsearch
        verify(mockBookSearchRepository, times(0)).save(book);
    }

    @Test
    @Transactional
    public void deleteBook() throws Exception {
        // Initialize the database
        bookService.save(book);

        int databaseSizeBeforeDelete = bookRepository.findAll().size();

        // Delete the book
        restBookMockMvc.perform(delete("/api/books/{id}", book.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Book in Elasticsearch
        verify(mockBookSearchRepository, times(1)).deleteById(book.getId());
    }

    @Test
    @Transactional
    public void searchBook() throws Exception {
        // Initialize the database
        bookService.save(book);
        when(mockBookSearchRepository.search(queryStringQuery("id:" + book.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(book), PageRequest.of(0, 1), 1));
        // Search the book
        restBookMockMvc.perform(get("/api/_search/books?query=id:" + book.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].publishYear").value(hasItem(DEFAULT_PUBLISH_YEAR)))
            .andExpect(jsonPath("$.[*].copies").value(hasItem(DEFAULT_COPIES)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64Utils.encodeToString(DEFAULT_COVER))));
    }
}
