import dayjs from 'dayjs/esm';

import { IMarketPrice, NewMarketPrice } from './market-price.model';

export const sampleWithRequiredData: IMarketPrice = {
  id: 5914,
  source: 'POKEMON_TCG_API',
  currency: 'underneath',
  lastUpdated: dayjs('2025-09-14T22:39'),
};

export const sampleWithPartialData: IMarketPrice = {
  id: 21114,
  source: 'POKEMON_TCG_API',
  currency: 'each lined ouch',
  priceLow: 24916.48,
  priceHigh: 22867.93,
  lastUpdated: dayjs('2025-09-14T22:15'),
};

export const sampleWithFullData: IMarketPrice = {
  id: 26285,
  source: 'TCGPLAYER',
  currency: 'guilt coarse whistle',
  priceLow: 23484.57,
  priceMid: 2423.58,
  priceHigh: 19553.8,
  lastUpdated: dayjs('2025-09-15T14:23'),
};

export const sampleWithNewData: NewMarketPrice = {
  source: 'TCGPLAYER',
  currency: 'sizzle sport pharmacopoeia',
  lastUpdated: dayjs('2025-09-14T20:20'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
