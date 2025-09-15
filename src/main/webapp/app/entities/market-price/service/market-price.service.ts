import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMarketPrice, NewMarketPrice } from '../market-price.model';

export type PartialUpdateMarketPrice = Partial<IMarketPrice> & Pick<IMarketPrice, 'id'>;

type RestOf<T extends IMarketPrice | NewMarketPrice> = Omit<T, 'lastUpdated'> & {
  lastUpdated?: string | null;
};

export type RestMarketPrice = RestOf<IMarketPrice>;

export type NewRestMarketPrice = RestOf<NewMarketPrice>;

export type PartialUpdateRestMarketPrice = RestOf<PartialUpdateMarketPrice>;

export type EntityResponseType = HttpResponse<IMarketPrice>;
export type EntityArrayResponseType = HttpResponse<IMarketPrice[]>;

@Injectable({ providedIn: 'root' })
export class MarketPriceService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/market-prices');

  create(marketPrice: NewMarketPrice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(marketPrice);
    return this.http
      .post<RestMarketPrice>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(marketPrice: IMarketPrice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(marketPrice);
    return this.http
      .put<RestMarketPrice>(`${this.resourceUrl}/${this.getMarketPriceIdentifier(marketPrice)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(marketPrice: PartialUpdateMarketPrice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(marketPrice);
    return this.http
      .patch<RestMarketPrice>(`${this.resourceUrl}/${this.getMarketPriceIdentifier(marketPrice)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMarketPrice>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMarketPrice[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMarketPriceIdentifier(marketPrice: Pick<IMarketPrice, 'id'>): number {
    return marketPrice.id;
  }

  compareMarketPrice(o1: Pick<IMarketPrice, 'id'> | null, o2: Pick<IMarketPrice, 'id'> | null): boolean {
    return o1 && o2 ? this.getMarketPriceIdentifier(o1) === this.getMarketPriceIdentifier(o2) : o1 === o2;
  }

  addMarketPriceToCollectionIfMissing<Type extends Pick<IMarketPrice, 'id'>>(
    marketPriceCollection: Type[],
    ...marketPricesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const marketPrices: Type[] = marketPricesToCheck.filter(isPresent);
    if (marketPrices.length > 0) {
      const marketPriceCollectionIdentifiers = marketPriceCollection.map(marketPriceItem => this.getMarketPriceIdentifier(marketPriceItem));
      const marketPricesToAdd = marketPrices.filter(marketPriceItem => {
        const marketPriceIdentifier = this.getMarketPriceIdentifier(marketPriceItem);
        if (marketPriceCollectionIdentifiers.includes(marketPriceIdentifier)) {
          return false;
        }
        marketPriceCollectionIdentifiers.push(marketPriceIdentifier);
        return true;
      });
      return [...marketPricesToAdd, ...marketPriceCollection];
    }
    return marketPriceCollection;
  }

  protected convertDateFromClient<T extends IMarketPrice | NewMarketPrice | PartialUpdateMarketPrice>(marketPrice: T): RestOf<T> {
    return {
      ...marketPrice,
      lastUpdated: marketPrice.lastUpdated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restMarketPrice: RestMarketPrice): IMarketPrice {
    return {
      ...restMarketPrice,
      lastUpdated: restMarketPrice.lastUpdated ? dayjs(restMarketPrice.lastUpdated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMarketPrice>): HttpResponse<IMarketPrice> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMarketPrice[]>): HttpResponse<IMarketPrice[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
