import { ICard, NewCard } from './card.model';

export const sampleWithRequiredData: ICard = {
  id: 10895,
  tcgId: 'yet',
  setCode: 'pry',
  number: 'inculcate for sticky',
  name: 'specific instead likely',
};

export const sampleWithPartialData: ICard = {
  id: 21889,
  tcgId: 'unfortunately yeast',
  setCode: 'ew ultimately',
  number: 'sleepily which',
  name: 'direct around heavenly',
  rarity: 'lest',
  superType: 'kindly pish knife',
  types: 'gadzooks',
  legalities: 'plump',
};

export const sampleWithFullData: ICard = {
  id: 24506,
  tcgId: 'dial',
  setCode: 'instead brave',
  number: 'store once kick',
  name: 'yesterday',
  rarity: 'indeed parody',
  superType: 'unless quicker suddenly',
  types: 'sheepishly than',
  imageUrl: 'oof ignorant general',
  legalities: 'aboard',
};

export const sampleWithNewData: NewCard = {
  tcgId: 'rectangular cautiously',
  setCode: 'out fondly',
  number: 'bare',
  name: 'swerve even operating',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
