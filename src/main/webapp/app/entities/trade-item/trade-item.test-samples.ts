import { ITradeItem, NewTradeItem } from './trade-item.model';

export const sampleWithRequiredData: ITradeItem = {
  id: 20113,
  quantity: 10431,
  side: 'monocle apt',
};

export const sampleWithPartialData: ITradeItem = {
  id: 10505,
  quantity: 23982,
  side: 'via',
};

export const sampleWithFullData: ITradeItem = {
  id: 13206,
  quantity: 22443,
  side: 'whoever',
};

export const sampleWithNewData: NewTradeItem = {
  quantity: 18873,
  side: 'boiling alongside opposite',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
