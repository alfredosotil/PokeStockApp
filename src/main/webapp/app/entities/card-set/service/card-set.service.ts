import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICardSet, NewCardSet } from '../card-set.model';

export type PartialUpdateCardSet = Partial<ICardSet> & Pick<ICardSet, 'id'>;

type RestOf<T extends ICardSet | NewCardSet> = Omit<T, 'releaseDate'> & {
  releaseDate?: string | null;
};

export type RestCardSet = RestOf<ICardSet>;

export type NewRestCardSet = RestOf<NewCardSet>;

export type PartialUpdateRestCardSet = RestOf<PartialUpdateCardSet>;

export type EntityResponseType = HttpResponse<ICardSet>;
export type EntityArrayResponseType = HttpResponse<ICardSet[]>;

@Injectable({ providedIn: 'root' })
export class CardSetService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/card-sets');

  create(cardSet: NewCardSet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cardSet);
    return this.http
      .post<RestCardSet>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(cardSet: ICardSet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cardSet);
    return this.http
      .put<RestCardSet>(`${this.resourceUrl}/${this.getCardSetIdentifier(cardSet)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(cardSet: PartialUpdateCardSet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cardSet);
    return this.http
      .patch<RestCardSet>(`${this.resourceUrl}/${this.getCardSetIdentifier(cardSet)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCardSet>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCardSet[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCardSetIdentifier(cardSet: Pick<ICardSet, 'id'>): number {
    return cardSet.id;
  }

  compareCardSet(o1: Pick<ICardSet, 'id'> | null, o2: Pick<ICardSet, 'id'> | null): boolean {
    return o1 && o2 ? this.getCardSetIdentifier(o1) === this.getCardSetIdentifier(o2) : o1 === o2;
  }

  addCardSetToCollectionIfMissing<Type extends Pick<ICardSet, 'id'>>(
    cardSetCollection: Type[],
    ...cardSetsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cardSets: Type[] = cardSetsToCheck.filter(isPresent);
    if (cardSets.length > 0) {
      const cardSetCollectionIdentifiers = cardSetCollection.map(cardSetItem => this.getCardSetIdentifier(cardSetItem));
      const cardSetsToAdd = cardSets.filter(cardSetItem => {
        const cardSetIdentifier = this.getCardSetIdentifier(cardSetItem);
        if (cardSetCollectionIdentifiers.includes(cardSetIdentifier)) {
          return false;
        }
        cardSetCollectionIdentifiers.push(cardSetIdentifier);
        return true;
      });
      return [...cardSetsToAdd, ...cardSetCollection];
    }
    return cardSetCollection;
  }

  protected convertDateFromClient<T extends ICardSet | NewCardSet | PartialUpdateCardSet>(cardSet: T): RestOf<T> {
    return {
      ...cardSet,
      releaseDate: cardSet.releaseDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCardSet: RestCardSet): ICardSet {
    return {
      ...restCardSet,
      releaseDate: restCardSet.releaseDate ? dayjs(restCardSet.releaseDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCardSet>): HttpResponse<ICardSet> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCardSet[]>): HttpResponse<ICardSet[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
