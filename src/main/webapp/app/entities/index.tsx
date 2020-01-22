import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Publisher from './publisher';
import Author from './author';
import Client from './client';
import Book from './book';
import BorrowedBook from './borrowed-book';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}publisher`} component={Publisher} />
      <ErrorBoundaryRoute path={`${match.url}author`} component={Author} />
      <ErrorBoundaryRoute path={`${match.url}client`} component={Client} />
      <ErrorBoundaryRoute path={`${match.url}book`} component={Book} />
      <ErrorBoundaryRoute path={`${match.url}borrowed-book`} component={BorrowedBook} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
