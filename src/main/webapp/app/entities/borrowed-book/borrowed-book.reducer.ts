import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IBorrowedBook, defaultValue } from 'app/shared/model/borrowed-book.model';

export const ACTION_TYPES = {
  SEARCH_BORROWEDBOOKS: 'borrowedBook/SEARCH_BORROWEDBOOKS',
  FETCH_BORROWEDBOOK_LIST: 'borrowedBook/FETCH_BORROWEDBOOK_LIST',
  FETCH_BORROWEDBOOK: 'borrowedBook/FETCH_BORROWEDBOOK',
  CREATE_BORROWEDBOOK: 'borrowedBook/CREATE_BORROWEDBOOK',
  UPDATE_BORROWEDBOOK: 'borrowedBook/UPDATE_BORROWEDBOOK',
  DELETE_BORROWEDBOOK: 'borrowedBook/DELETE_BORROWEDBOOK',
  RESET: 'borrowedBook/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IBorrowedBook>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type BorrowedBookState = Readonly<typeof initialState>;

// Reducer

export default (state: BorrowedBookState = initialState, action): BorrowedBookState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_BORROWEDBOOKS):
    case REQUEST(ACTION_TYPES.FETCH_BORROWEDBOOK_LIST):
    case REQUEST(ACTION_TYPES.FETCH_BORROWEDBOOK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_BORROWEDBOOK):
    case REQUEST(ACTION_TYPES.UPDATE_BORROWEDBOOK):
    case REQUEST(ACTION_TYPES.DELETE_BORROWEDBOOK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_BORROWEDBOOKS):
    case FAILURE(ACTION_TYPES.FETCH_BORROWEDBOOK_LIST):
    case FAILURE(ACTION_TYPES.FETCH_BORROWEDBOOK):
    case FAILURE(ACTION_TYPES.CREATE_BORROWEDBOOK):
    case FAILURE(ACTION_TYPES.UPDATE_BORROWEDBOOK):
    case FAILURE(ACTION_TYPES.DELETE_BORROWEDBOOK):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_BORROWEDBOOKS):
    case SUCCESS(ACTION_TYPES.FETCH_BORROWEDBOOK_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    case SUCCESS(ACTION_TYPES.FETCH_BORROWEDBOOK):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_BORROWEDBOOK):
    case SUCCESS(ACTION_TYPES.UPDATE_BORROWEDBOOK):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_BORROWEDBOOK):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/borrowed-books';
const apiSearchUrl = 'api/_search/borrowed-books';

// Actions

export const getSearchEntities: ICrudSearchAction<IBorrowedBook> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_BORROWEDBOOKS,
  payload: axios.get<IBorrowedBook>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IBorrowedBook> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_BORROWEDBOOK_LIST,
    payload: axios.get<IBorrowedBook>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IBorrowedBook> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_BORROWEDBOOK,
    payload: axios.get<IBorrowedBook>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IBorrowedBook> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_BORROWEDBOOK,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IBorrowedBook> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_BORROWEDBOOK,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IBorrowedBook> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_BORROWEDBOOK,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
