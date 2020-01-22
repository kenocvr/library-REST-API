package com.library.prod.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link BorrowedBookSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class BorrowedBookSearchRepositoryMockConfiguration {

    @MockBean
    private BorrowedBookSearchRepository mockBorrowedBookSearchRepository;

}
