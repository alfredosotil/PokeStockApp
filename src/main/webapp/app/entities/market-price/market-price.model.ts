import dayjs from 'dayjs/esm';
import { ICard } from 'app/entities/card/card.model';
import { MarketSource } from 'app/entities/enumerations/market-source.model';

export interface IMarketPrice {
  id: number;
  source?: keyof typeof MarketSource | null;
  currency?: string | null;
  priceLow?: number | null;
  priceMid?: number | null;
  priceHigh?: number | null;
  lastUpdated?: dayjs.Dayjs | null;
  card?: Pick<ICard, 'id' | 'name'> | null;
}

export type NewMarketPrice = Omit<IMarketPrice, 'id'> & { id: null };
