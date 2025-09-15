import { IUser } from 'app/entities/user/user.model';

export interface IUserProfile {
  id: number;
  bio?: string | null;
  location?: string | null;
  favoriteSet?: string | null;
  playstyle?: string | null;
  avatar?: string | null;
  avatarContentType?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewUserProfile = Omit<IUserProfile, 'id'> & { id: null };
