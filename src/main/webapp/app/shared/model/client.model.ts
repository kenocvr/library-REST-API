export interface IClient {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  address?: string;
  phone?: string;
}

export const defaultValue: Readonly<IClient> = {};
