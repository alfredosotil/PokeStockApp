import { ITrade } from 'app/entities/trade/trade.model';
import { ICard } from 'app/entities/card/card.model';

export interface ITradeItem {
  id: number;
  quantity?: number | null;
  side?: string | null;
  trade?: Pick<ITrade, 'id'> | null;
  card?: Pick<ICard, 'id' | 'name'> | null;
}

export type NewTradeItem = Omit<ITradeItem, 'id'> & { id: null };
