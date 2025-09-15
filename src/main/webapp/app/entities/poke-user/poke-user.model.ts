import { IUser } from 'app/entities/user/user.model';

export interface IPokeUser {
  id: number;
  displayName?: string | null;
  country?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewPokeUser = Omit<IPokeUser, 'id'> & { id: null };
