import { IBook } from 'app/shared/model/book.model';

export interface IAuthor {
  id?: number;
  firstName?: string;
  lastName?: string;
  books?: IBook[];
}

export const defaultValue: Readonly<IAuthor> = {};
