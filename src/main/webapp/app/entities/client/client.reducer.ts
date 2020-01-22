import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IClient, defaultValue } from 'app/shared/model/client.model';

export const ACTION_TYPES = {
  SEARCH_CLIENTS: 'client/SEARCH_CLIENTS',
  FETCH_CLIENT_LIST: 'client/FETCH_CLIENT_LIST',
  FETCH_CLIENT: 'client/FETCH_CLIENT',
  CREATE_CLIENT: 'client/CREATE_CLIENT',
  UPDATE_CLIENT: 'client/UPDATE_CLIENT',
  DELETE_CLIENT: 'client/DELETE_CLIENT',
  RESET: 'client/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IClient>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type ClientState = Readonly<typeof initialState>;

// Reducer

export default (state: ClientState = initialState, action): ClientState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_CLIENTS):
    case REQUEST(ACTION_TYPES.FETCH_CLIENT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_CLIENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_CLIENT):
    case REQUEST(ACTION_TYPES.UPDATE_CLIENT):
    case REQUEST(ACTION_TYPES.DELETE_CLIENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_CLIENTS):
    case FAILURE(ACTION_TYPES.FETCH_CLIENT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_CLIENT):
    case FAILURE(ACTION_TYPES.CREATE_CLIENT):
    case FAILURE(ACTION_TYPES.UPDATE_CLIENT):
    case FAILURE(ACTION_TYPES.DELETE_CLIENT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_CLIENTS):
    case SUCCESS(ACTION_TYPES.FETCH_CLIENT_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    case SUCCESS(ACTION_TYPES.FETCH_CLIENT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_CLIENT):
    case SUCCESS(ACTION_TYPES.UPDATE_CLIENT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_CLIENT):
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

const apiUrl = 'api/clients';
const apiSearchUrl = 'api/_search/clients';

// Actions

export const getSearchEntities: ICrudSearchAction<IClient> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_CLIENTS,
  payload: axios.get<IClient>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IClient> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_CLIENT_LIST,
    payload: axios.get<IClient>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IClient> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_CLIENT,
    payload: axios.get<IClient>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IClient> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_CLIENT,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IClient> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_CLIENT,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IClient> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_CLIENT,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
