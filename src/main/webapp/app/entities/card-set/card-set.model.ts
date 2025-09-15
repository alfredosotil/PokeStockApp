import dayjs from 'dayjs/esm';

export interface ICardSet {
  id: number;
  code?: string | null;
  name?: string | null;
  releaseDate?: dayjs.Dayjs | null;
}

export type NewCardSet = Omit<ICardSet, 'id'> & { id: null };
