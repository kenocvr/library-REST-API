import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import BorrowedBook from './borrowed-book';
import BorrowedBookDetail from './borrowed-book-detail';
import BorrowedBookUpdate from './borrowed-book-update';
import BorrowedBookDeleteDialog from './borrowed-book-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={BorrowedBookDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BorrowedBookUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BorrowedBookUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BorrowedBookDetail} />
      <ErrorBoundaryRoute path={match.url} component={BorrowedBook} />
    </Switch>
  </>
);

export default Routes;
