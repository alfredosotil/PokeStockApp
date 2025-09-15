import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITradeItem, NewTradeItem } from '../trade-item.model';

export type PartialUpdateTradeItem = Partial<ITradeItem> & Pick<ITradeItem, 'id'>;

export type EntityResponseType = HttpResponse<ITradeItem>;
export type EntityArrayResponseType = HttpResponse<ITradeItem[]>;

@Injectable({ providedIn: 'root' })
export class TradeItemService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/trade-items');

  create(tradeItem: NewTradeItem): Observable<EntityResponseType> {
    return this.http.post<ITradeItem>(this.resourceUrl, tradeItem, { observe: 'response' });
  }

  update(tradeItem: ITradeItem): Observable<EntityResponseType> {
    return this.http.put<ITradeItem>(`${this.resourceUrl}/${this.getTradeItemIdentifier(tradeItem)}`, tradeItem, { observe: 'response' });
  }

  partialUpdate(tradeItem: PartialUpdateTradeItem): Observable<EntityResponseType> {
    return this.http.patch<ITradeItem>(`${this.resourceUrl}/${this.getTradeItemIdentifier(tradeItem)}`, tradeItem, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITradeItem>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITradeItem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTradeItemIdentifier(tradeItem: Pick<ITradeItem, 'id'>): number {
    return tradeItem.id;
  }

  compareTradeItem(o1: Pick<ITradeItem, 'id'> | null, o2: Pick<ITradeItem, 'id'> | null): boolean {
    return o1 && o2 ? this.getTradeItemIdentifier(o1) === this.getTradeItemIdentifier(o2) : o1 === o2;
  }

  addTradeItemToCollectionIfMissing<Type extends Pick<ITradeItem, 'id'>>(
    tradeItemCollection: Type[],
    ...tradeItemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tradeItems: Type[] = tradeItemsToCheck.filter(isPresent);
    if (tradeItems.length > 0) {
      const tradeItemCollectionIdentifiers = tradeItemCollection.map(tradeItemItem => this.getTradeItemIdentifier(tradeItemItem));
      const tradeItemsToAdd = tradeItems.filter(tradeItemItem => {
        const tradeItemIdentifier = this.getTradeItemIdentifier(tradeItemItem);
        if (tradeItemCollectionIdentifiers.includes(tradeItemIdentifier)) {
          return false;
        }
        tradeItemCollectionIdentifiers.push(tradeItemIdentifier);
        return true;
      });
      return [...tradeItemsToAdd, ...tradeItemCollection];
    }
    return tradeItemCollection;
  }
}
