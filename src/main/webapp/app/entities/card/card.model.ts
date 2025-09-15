import { ICardSet } from 'app/entities/card-set/card-set.model';

export interface ICard {
  id: number;
  tcgId?: string | null;
  setCode?: string | null;
  number?: string | null;
  name?: string | null;
  rarity?: string | null;
  superType?: string | null;
  types?: string | null;
  imageUrl?: string | null;
  legalities?: string | null;
  set?: Pick<ICardSet, 'id' | 'code'> | null;
}

export type NewCard = Omit<ICard, 'id'> & { id: null };
