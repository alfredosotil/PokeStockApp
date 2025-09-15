import { IPokeUser, NewPokeUser } from './poke-user.model';

export const sampleWithRequiredData: IPokeUser = {
  id: 13095,
  displayName: 'suddenly mmm improbable',
};

export const sampleWithPartialData: IPokeUser = {
  id: 16146,
  displayName: 'irresponsible tough however',
  country: 'Irlanda',
};

export const sampleWithFullData: IPokeUser = {
  id: 2038,
  displayName: 'gummy doing openly',
  country: 'Bermuda',
};

export const sampleWithNewData: NewPokeUser = {
  displayName: 'daily striking',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
