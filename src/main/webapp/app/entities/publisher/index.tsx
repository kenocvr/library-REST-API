import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Publisher from './publisher';
import PublisherDetail from './publisher-detail';
import PublisherUpdate from './publisher-update';
import PublisherDeleteDialog from './publisher-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={PublisherDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PublisherUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PublisherUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PublisherDetail} />
      <ErrorBoundaryRoute path={match.url} component={Publisher} />
    </Switch>
  </>
);

export default Routes;
