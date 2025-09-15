import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInventoryItem, NewInventoryItem } from '../inventory-item.model';

export type PartialUpdateInventoryItem = Partial<IInventoryItem> & Pick<IInventoryItem, 'id'>;

export type EntityResponseType = HttpResponse<IInventoryItem>;
export type EntityArrayResponseType = HttpResponse<IInventoryItem[]>;

@Injectable({ providedIn: 'root' })
export class InventoryItemService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/inventory-items');

  create(inventoryItem: NewInventoryItem): Observable<EntityResponseType> {
    return this.http.post<IInventoryItem>(this.resourceUrl, inventoryItem, { observe: 'response' });
  }

  update(inventoryItem: IInventoryItem): Observable<EntityResponseType> {
    return this.http.put<IInventoryItem>(`${this.resourceUrl}/${this.getInventoryItemIdentifier(inventoryItem)}`, inventoryItem, {
      observe: 'response',
    });
  }

  partialUpdate(inventoryItem: PartialUpdateInventoryItem): Observable<EntityResponseType> {
    return this.http.patch<IInventoryItem>(`${this.resourceUrl}/${this.getInventoryItemIdentifier(inventoryItem)}`, inventoryItem, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IInventoryItem>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IInventoryItem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getInventoryItemIdentifier(inventoryItem: Pick<IInventoryItem, 'id'>): number {
    return inventoryItem.id;
  }

  compareInventoryItem(o1: Pick<IInventoryItem, 'id'> | null, o2: Pick<IInventoryItem, 'id'> | null): boolean {
    return o1 && o2 ? this.getInventoryItemIdentifier(o1) === this.getInventoryItemIdentifier(o2) : o1 === o2;
  }

  addInventoryItemToCollectionIfMissing<Type extends Pick<IInventoryItem, 'id'>>(
    inventoryItemCollection: Type[],
    ...inventoryItemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const inventoryItems: Type[] = inventoryItemsToCheck.filter(isPresent);
    if (inventoryItems.length > 0) {
      const inventoryItemCollectionIdentifiers = inventoryItemCollection.map(inventoryItemItem =>
        this.getInventoryItemIdentifier(inventoryItemItem),
      );
      const inventoryItemsToAdd = inventoryItems.filter(inventoryItemItem => {
        const inventoryItemIdentifier = this.getInventoryItemIdentifier(inventoryItemItem);
        if (inventoryItemCollectionIdentifiers.includes(inventoryItemIdentifier)) {
          return false;
        }
        inventoryItemCollectionIdentifiers.push(inventoryItemIdentifier);
        return true;
      });
      return [...inventoryItemsToAdd, ...inventoryItemCollection];
    }
    return inventoryItemCollection;
  }
}
