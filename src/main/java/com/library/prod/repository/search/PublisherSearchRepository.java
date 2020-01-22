package com.library.prod.repository.search;

import com.library.prod.domain.Publisher;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Publisher} entity.
 */
public interface PublisherSearchRepository extends ElasticsearchRepository<Publisher, Long> {
}
