import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 10110,
  type: 'TRADE_STATUS',
  title: 'neighboring',
  message: '../fake-data/blob/hipster.txt',
  read: true,
  createdAt: dayjs('2025-09-14T22:23'),
};

export const sampleWithPartialData: INotification = {
  id: 24804,
  type: 'PRICE_ALERT',
  title: 'during ouch',
  message: '../fake-data/blob/hipster.txt',
  read: false,
  createdAt: dayjs('2025-09-15T00:11'),
};

export const sampleWithFullData: INotification = {
  id: 5787,
  type: 'TRADE_PROPOSAL',
  title: 'inferior boo bruised',
  message: '../fake-data/blob/hipster.txt',
  read: false,
  createdAt: dayjs('2025-09-14T21:01'),
  linkUrl: 'devil why impure',
};

export const sampleWithNewData: NewNotification = {
  type: 'SYSTEM',
  title: 'usually casement vein',
  message: '../fake-data/blob/hipster.txt',
  read: true,
  createdAt: dayjs('2025-09-15T14:41'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
