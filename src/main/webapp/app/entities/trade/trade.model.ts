import dayjs from 'dayjs/esm';
import { IPokeUser } from 'app/entities/poke-user/poke-user.model';
import { TradeStatus } from 'app/entities/enumerations/trade-status.model';

export interface ITrade {
  id: number;
  status?: keyof typeof TradeStatus | null;
  message?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  proposer?: Pick<IPokeUser, 'id' | 'displayName'> | null;
}

export type NewTrade = Omit<ITrade, 'id'> & { id: null };
