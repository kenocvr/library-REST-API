import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, setFileData, openFile, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IPublisher } from 'app/shared/model/publisher.model';
import { getEntities as getPublishers } from 'app/entities/publisher/publisher.reducer';
import { IAuthor } from 'app/shared/model/author.model';
import { getEntities as getAuthors } from 'app/entities/author/author.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './book.reducer';
import { IBook } from 'app/shared/model/book.model';
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IBookUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const BookUpdate = (props: IBookUpdateProps) => {
  const [idsauthor, setIdsauthor] = useState([]);
  const [publisherId, setPublisherId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { bookEntity, publishers, authors, loading, updating } = props;

  const { cover, coverContentType } = bookEntity;

  const handleClose = () => {
    props.history.push('/book' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getPublishers();
    props.getAuthors();
  }, []);

  const onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => props.setBlob(name, data, contentType), isAnImage);
  };

  const clearBlob = name => () => {
    props.setBlob(name, undefined, undefined);
  };

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...bookEntity,
        ...values,
        authors: mapIdList(values.authors)
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="libraryApp.book.home.createOrEditLabel">Create or edit a Book</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : bookEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="book-id">ID</Label>
                  <AvInput id="book-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="isbnLabel" for="book-isbn">
                  Isbn
                </Label>
                <AvField
                  id="book-isbn"
                  type="text"
                  name="isbn"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                    minLength: { value: 5, errorMessage: 'This field is required to be at least 5 characters.' },
                    maxLength: { value: 13, errorMessage: 'This field cannot be longer than 13 characters.' }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="nameLabel" for="book-name">
                  Name
                </Label>
                <AvField
                  id="book-name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                    maxLength: { value: 100, errorMessage: 'This field cannot be longer than 100 characters.' }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="publishYearLabel" for="book-publishYear">
                  Publish Year
                </Label>
                <AvField
                  id="book-publishYear"
                  type="text"
                  name="publishYear"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                    minLength: { value: 4, errorMessage: 'This field is required to be at least 4 characters.' },
                    maxLength: { value: 50, errorMessage: 'This field cannot be longer than 50 characters.' }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="copiesLabel" for="book-copies">
                  Copies
                </Label>
                <AvField
                  id="book-copies"
                  type="string"
                  className="form-control"
                  name="copies"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                    number: { value: true, errorMessage: 'This field should be a number.' }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <AvGroup>
                  <Label id="coverLabel" for="cover">
                    Cover
                  </Label>
                  <br />
                  {cover ? (
                    <div>
                      <a onClick={openFile(coverContentType, cover)}>
                        <img src={`data:${coverContentType};base64,${cover}`} style={{ maxHeight: '100px' }} />
                      </a>
                      <br />
                      <Row>
                        <Col md="11">
                          <span>
                            {coverContentType}, {byteSize(cover)}
                          </span>
                        </Col>
                        <Col md="1">
                          <Button color="danger" onClick={clearBlob('cover')}>
                            <FontAwesomeIcon icon="times-circle" />
                          </Button>
                        </Col>
                      </Row>
                    </div>
                  ) : null}
                  <input id="file_cover" type="file" onChange={onBlobChange(true, 'cover')} accept="image/*" />
                  <AvInput type="hidden" name="cover" value={cover} />
                </AvGroup>
              </AvGroup>
              <AvGroup>
                <Label for="book-publisher">Publisher</Label>
                <AvInput id="book-publisher" type="select" className="form-control" name="publisher.id">
                  <option value="" key="0" />
                  {publishers
                    ? publishers.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.name}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="book-author">Author</Label>
                <AvInput
                  id="book-author"
                  type="select"
                  multiple
                  className="form-control"
                  name="authors"
                  value={bookEntity.authors && bookEntity.authors.map(e => e.id)}
                >
                  <option value="" key="0" />
                  {authors
                    ? authors.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.firstName}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/book" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  publishers: storeState.publisher.entities,
  authors: storeState.author.entities,
  bookEntity: storeState.book.entity,
  loading: storeState.book.loading,
  updating: storeState.book.updating,
  updateSuccess: storeState.book.updateSuccess
});

const mapDispatchToProps = {
  getPublishers,
  getAuthors,
  getEntity,
  updateEntity,
  setBlob,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(BookUpdate);
