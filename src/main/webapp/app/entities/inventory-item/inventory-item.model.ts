import { ICard } from 'app/entities/card/card.model';
import { IPokeUser } from 'app/entities/poke-user/poke-user.model';
import { CardCondition } from 'app/entities/enumerations/card-condition.model';
import { CardLanguage } from 'app/entities/enumerations/card-language.model';

export interface IInventoryItem {
  id: number;
  quantity?: number | null;
  condition?: keyof typeof CardCondition | null;
  language?: keyof typeof CardLanguage | null;
  graded?: boolean | null;
  grade?: string | null;
  purchasePrice?: number | null;
  notes?: string | null;
  card?: Pick<ICard, 'id' | 'name'> | null;
  owner?: Pick<IPokeUser, 'id' | 'displayName'> | null;
}

export type NewInventoryItem = Omit<IInventoryItem, 'id'> & { id: null };
