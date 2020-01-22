import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Client from './client';
import ClientDetail from './client-detail';
import ClientUpdate from './client-update';
import ClientDeleteDialog from './client-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ClientDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ClientUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ClientUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ClientDetail} />
      <ErrorBoundaryRoute path={match.url} component={Client} />
    </Switch>
  </>
);

export default Routes;
