import dayjs from 'dayjs/esm';

import { ICardSet, NewCardSet } from './card-set.model';

export const sampleWithRequiredData: ICardSet = {
  id: 23843,
  code: 'gosh mundane',
  name: 'ugh',
};

export const sampleWithPartialData: ICardSet = {
  id: 14075,
  code: 'gee through',
  name: 'although yowza',
  releaseDate: dayjs('2025-09-15T05:29'),
};

export const sampleWithFullData: ICardSet = {
  id: 18654,
  code: 'forage',
  name: 'confound quip',
  releaseDate: dayjs('2025-09-15T07:02'),
};

export const sampleWithNewData: NewCardSet = {
  code: 'surprised',
  name: 'lazily rebuke',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
