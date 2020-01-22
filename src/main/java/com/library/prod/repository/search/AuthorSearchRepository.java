package com.library.prod.repository.search;

import com.library.prod.domain.Author;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Author} entity.
 */
public interface AuthorSearchRepository extends ElasticsearchRepository<Author, Long> {
}
