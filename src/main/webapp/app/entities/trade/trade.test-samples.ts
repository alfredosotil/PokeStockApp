import dayjs from 'dayjs/esm';

import { ITrade, NewTrade } from './trade.model';

export const sampleWithRequiredData: ITrade = {
  id: 24045,
  status: 'CANCELLED',
  createdAt: dayjs('2025-09-15T12:16'),
};

export const sampleWithPartialData: ITrade = {
  id: 7649,
  status: 'COMPLETED',
  message: 'pish obesity',
  createdAt: dayjs('2025-09-14T20:29'),
};

export const sampleWithFullData: ITrade = {
  id: 15971,
  status: 'PROPOSED',
  message: 'vice gad ack',
  createdAt: dayjs('2025-09-15T04:11'),
  updatedAt: dayjs('2025-09-15T09:25'),
};

export const sampleWithNewData: NewTrade = {
  status: 'COMPLETED',
  createdAt: dayjs('2025-09-15T03:40'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
