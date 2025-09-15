import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrade, NewTrade } from '../trade.model';

export type PartialUpdateTrade = Partial<ITrade> & Pick<ITrade, 'id'>;

type RestOf<T extends ITrade | NewTrade> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type RestTrade = RestOf<ITrade>;

export type NewRestTrade = RestOf<NewTrade>;

export type PartialUpdateRestTrade = RestOf<PartialUpdateTrade>;

export type EntityResponseType = HttpResponse<ITrade>;
export type EntityArrayResponseType = HttpResponse<ITrade[]>;

@Injectable({ providedIn: 'root' })
export class TradeService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/trades');

  create(trade: NewTrade): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trade);
    return this.http.post<RestTrade>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trade: ITrade): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trade);
    return this.http
      .put<RestTrade>(`${this.resourceUrl}/${this.getTradeIdentifier(trade)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trade: PartialUpdateTrade): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trade);
    return this.http
      .patch<RestTrade>(`${this.resourceUrl}/${this.getTradeIdentifier(trade)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrade>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrade[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTradeIdentifier(trade: Pick<ITrade, 'id'>): number {
    return trade.id;
  }

  compareTrade(o1: Pick<ITrade, 'id'> | null, o2: Pick<ITrade, 'id'> | null): boolean {
    return o1 && o2 ? this.getTradeIdentifier(o1) === this.getTradeIdentifier(o2) : o1 === o2;
  }

  addTradeToCollectionIfMissing<Type extends Pick<ITrade, 'id'>>(
    tradeCollection: Type[],
    ...tradesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trades: Type[] = tradesToCheck.filter(isPresent);
    if (trades.length > 0) {
      const tradeCollectionIdentifiers = tradeCollection.map(tradeItem => this.getTradeIdentifier(tradeItem));
      const tradesToAdd = trades.filter(tradeItem => {
        const tradeIdentifier = this.getTradeIdentifier(tradeItem);
        if (tradeCollectionIdentifiers.includes(tradeIdentifier)) {
          return false;
        }
        tradeCollectionIdentifiers.push(tradeIdentifier);
        return true;
      });
      return [...tradesToAdd, ...tradeCollection];
    }
    return tradeCollection;
  }

  protected convertDateFromClient<T extends ITrade | NewTrade | PartialUpdateTrade>(trade: T): RestOf<T> {
    return {
      ...trade,
      createdAt: trade.createdAt?.toJSON() ?? null,
      updatedAt: trade.updatedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTrade: RestTrade): ITrade {
    return {
      ...restTrade,
      createdAt: restTrade.createdAt ? dayjs(restTrade.createdAt) : undefined,
      updatedAt: restTrade.updatedAt ? dayjs(restTrade.updatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTrade>): HttpResponse<ITrade> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTrade[]>): HttpResponse<ITrade[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
