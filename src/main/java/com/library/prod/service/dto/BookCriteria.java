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

/**
 * Criteria class for the {@link com.library.prod.domain.Book} entity. This class is used
 * in {@link com.library.prod.web.rest.BookResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /books?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BookCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter isbn;

    private StringFilter name;

    private StringFilter publishYear;

    private IntegerFilter copies;

    private LongFilter publisherId;

    private LongFilter authorId;

    public BookCriteria(){
    }

    public BookCriteria(BookCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.isbn = other.isbn == null ? null : other.isbn.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.publishYear = other.publishYear == null ? null : other.publishYear.copy();
        this.copies = other.copies == null ? null : other.copies.copy();
        this.publisherId = other.publisherId == null ? null : other.publisherId.copy();
        this.authorId = other.authorId == null ? null : other.authorId.copy();
    }

    @Override
    public BookCriteria copy() {
        return new BookCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getIsbn() {
        return isbn;
    }

    public void setIsbn(StringFilter isbn) {
        this.isbn = isbn;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(StringFilter publishYear) {
        this.publishYear = publishYear;
    }

    public IntegerFilter getCopies() {
        return copies;
    }

    public void setCopies(IntegerFilter copies) {
        this.copies = copies;
    }

    public LongFilter getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(LongFilter publisherId) {
        this.publisherId = publisherId;
    }

    public LongFilter getAuthorId() {
        return authorId;
    }

    public void setAuthorId(LongFilter authorId) {
        this.authorId = authorId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BookCriteria that = (BookCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(isbn, that.isbn) &&
            Objects.equals(name, that.name) &&
            Objects.equals(publishYear, that.publishYear) &&
            Objects.equals(copies, that.copies) &&
            Objects.equals(publisherId, that.publisherId) &&
            Objects.equals(authorId, that.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        isbn,
        name,
        publishYear,
        copies,
        publisherId,
        authorId
        );
    }

    @Override
    public String toString() {
        return "BookCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (isbn != null ? "isbn=" + isbn + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (publishYear != null ? "publishYear=" + publishYear + ", " : "") +
                (copies != null ? "copies=" + copies + ", " : "") +
                (publisherId != null ? "publisherId=" + publisherId + ", " : "") +
                (authorId != null ? "authorId=" + authorId + ", " : "") +
            "}";
    }

}
