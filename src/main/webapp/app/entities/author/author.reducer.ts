import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IAuthor, defaultValue } from 'app/shared/model/author.model';

export const ACTION_TYPES = {
  SEARCH_AUTHORS: 'author/SEARCH_AUTHORS',
  FETCH_AUTHOR_LIST: 'author/FETCH_AUTHOR_LIST',
  FETCH_AUTHOR: 'author/FETCH_AUTHOR',
  CREATE_AUTHOR: 'author/CREATE_AUTHOR',
  UPDATE_AUTHOR: 'author/UPDATE_AUTHOR',
  DELETE_AUTHOR: 'author/DELETE_AUTHOR',
  RESET: 'author/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IAuthor>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type AuthorState = Readonly<typeof initialState>;

// Reducer

export default (state: AuthorState = initialState, action): AuthorState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_AUTHORS):
    case REQUEST(ACTION_TYPES.FETCH_AUTHOR_LIST):
    case REQUEST(ACTION_TYPES.FETCH_AUTHOR):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_AUTHOR):
    case REQUEST(ACTION_TYPES.UPDATE_AUTHOR):
    case REQUEST(ACTION_TYPES.DELETE_AUTHOR):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_AUTHORS):
    case FAILURE(ACTION_TYPES.FETCH_AUTHOR_LIST):
    case FAILURE(ACTION_TYPES.FETCH_AUTHOR):
    case FAILURE(ACTION_TYPES.CREATE_AUTHOR):
    case FAILURE(ACTION_TYPES.UPDATE_AUTHOR):
    case FAILURE(ACTION_TYPES.DELETE_AUTHOR):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_AUTHORS):
    case SUCCESS(ACTION_TYPES.FETCH_AUTHOR_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    case SUCCESS(ACTION_TYPES.FETCH_AUTHOR):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_AUTHOR):
    case SUCCESS(ACTION_TYPES.UPDATE_AUTHOR):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_AUTHOR):
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

const apiUrl = 'api/authors';
const apiSearchUrl = 'api/_search/authors';

// Actions

export const getSearchEntities: ICrudSearchAction<IAuthor> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_AUTHORS,
  payload: axios.get<IAuthor>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IAuthor> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_AUTHOR_LIST,
    payload: axios.get<IAuthor>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IAuthor> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_AUTHOR,
    payload: axios.get<IAuthor>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IAuthor> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_AUTHOR,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IAuthor> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_AUTHOR,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IAuthor> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_AUTHOR,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
