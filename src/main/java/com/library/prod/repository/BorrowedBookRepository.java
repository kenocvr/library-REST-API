package com.library.prod.repository;

import com.library.prod.domain.BorrowedBook;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the BorrowedBook entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long>, JpaSpecificationExecutor<BorrowedBook> {

}
