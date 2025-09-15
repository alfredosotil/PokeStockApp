import { IUserProfile, NewUserProfile } from './user-profile.model';

export const sampleWithRequiredData: IUserProfile = {
  id: 4033,
};

export const sampleWithPartialData: IUserProfile = {
  id: 18199,
  bio: 'per under yet',
  location: 'cardboard ugh gleefully',
  favoriteSet: 'unrealistic in',
};

export const sampleWithFullData: IUserProfile = {
  id: 9570,
  bio: 'folklore glisten cuddly',
  location: 'guidance second',
  favoriteSet: 'yum access',
  playstyle: 'unless eek',
  avatar: '../fake-data/blob/hipster.png',
  avatarContentType: 'unknown',
};

export const sampleWithNewData: NewUserProfile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
