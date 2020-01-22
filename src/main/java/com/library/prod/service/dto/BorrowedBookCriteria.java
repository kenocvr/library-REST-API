package com.library.prod.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link com.library.prod.domain.BorrowedBook} entity. This class is used
 * in {@link com.library.prod.web.rest.BorrowedBookResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /borrowed-books?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BorrowedBookCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter borrowDate;

    private LongFilter bookId;

    private LongFilter clientId;

    public BorrowedBookCriteria(){
    }

    public BorrowedBookCriteria(BorrowedBookCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.borrowDate = other.borrowDate == null ? null : other.borrowDate.copy();
        this.bookId = other.bookId == null ? null : other.bookId.copy();
        this.clientId = other.clientId == null ? null : other.clientId.copy();
    }

    @Override
    public BorrowedBookCriteria copy() {
        return new BorrowedBookCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateFilter borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LongFilter getBookId() {
        return bookId;
    }

    public void setBookId(LongFilter bookId) {
        this.bookId = bookId;
    }

    public LongFilter getClientId() {
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BorrowedBookCriteria that = (BorrowedBookCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(borrowDate, that.borrowDate) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        borrowDate,
        bookId,
        clientId
        );
    }

    @Override
    public String toString() {
        return "BorrowedBookCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (borrowDate != null ? "borrowDate=" + borrowDate + ", " : "") +
                (bookId != null ? "bookId=" + bookId + ", " : "") +
                (clientId != null ? "clientId=" + clientId + ", " : "") +
            "}";
    }

}
