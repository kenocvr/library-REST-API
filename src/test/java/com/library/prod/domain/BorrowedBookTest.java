package com.library.prod.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.library.prod.web.rest.TestUtil;

public class BorrowedBookTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BorrowedBook.class);
        BorrowedBook borrowedBook1 = new BorrowedBook();
        borrowedBook1.setId(1L);
        BorrowedBook borrowedBook2 = new BorrowedBook();
        borrowedBook2.setId(borrowedBook1.getId());
        assertThat(borrowedBook1).isEqualTo(borrowedBook2);
        borrowedBook2.setId(2L);
        assertThat(borrowedBook1).isNotEqualTo(borrowedBook2);
        borrowedBook1.setId(null);
        assertThat(borrowedBook1).isNotEqualTo(borrowedBook2);
    }
}
