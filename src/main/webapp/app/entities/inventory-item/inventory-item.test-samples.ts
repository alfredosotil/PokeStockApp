import { IInventoryItem, NewInventoryItem } from './inventory-item.model';

export const sampleWithRequiredData: IInventoryItem = {
  id: 19345,
  quantity: 30136,
  condition: 'PLAYED',
  language: 'ES',
  graded: true,
};

export const sampleWithPartialData: IInventoryItem = {
  id: 15866,
  quantity: 25991,
  condition: 'LIGHT_PLAY',
  language: 'FR',
  graded: false,
  grade: 'pertinent excepting',
};

export const sampleWithFullData: IInventoryItem = {
  id: 20465,
  quantity: 16479,
  condition: 'VERY_GOOD',
  language: 'JP',
  graded: false,
  grade: 'moral jittery',
  purchasePrice: 13394.59,
  notes: 'malfunction secret lavish',
};

export const sampleWithNewData: NewInventoryItem = {
  quantity: 3813,
  condition: 'PLAYED',
  language: 'FR',
  graded: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
