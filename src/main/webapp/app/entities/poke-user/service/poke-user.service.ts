import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPokeUser, NewPokeUser } from '../poke-user.model';

export type PartialUpdatePokeUser = Partial<IPokeUser> & Pick<IPokeUser, 'id'>;

export type EntityResponseType = HttpResponse<IPokeUser>;
export type EntityArrayResponseType = HttpResponse<IPokeUser[]>;

@Injectable({ providedIn: 'root' })
export class PokeUserService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/poke-users');

  create(pokeUser: NewPokeUser): Observable<EntityResponseType> {
    return this.http.post<IPokeUser>(this.resourceUrl, pokeUser, { observe: 'response' });
  }

  update(pokeUser: IPokeUser): Observable<EntityResponseType> {
    return this.http.put<IPokeUser>(`${this.resourceUrl}/${this.getPokeUserIdentifier(pokeUser)}`, pokeUser, { observe: 'response' });
  }

  partialUpdate(pokeUser: PartialUpdatePokeUser): Observable<EntityResponseType> {
    return this.http.patch<IPokeUser>(`${this.resourceUrl}/${this.getPokeUserIdentifier(pokeUser)}`, pokeUser, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPokeUser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPokeUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPokeUserIdentifier(pokeUser: Pick<IPokeUser, 'id'>): number {
    return pokeUser.id;
  }

  comparePokeUser(o1: Pick<IPokeUser, 'id'> | null, o2: Pick<IPokeUser, 'id'> | null): boolean {
    return o1 && o2 ? this.getPokeUserIdentifier(o1) === this.getPokeUserIdentifier(o2) : o1 === o2;
  }

  addPokeUserToCollectionIfMissing<Type extends Pick<IPokeUser, 'id'>>(
    pokeUserCollection: Type[],
    ...pokeUsersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const pokeUsers: Type[] = pokeUsersToCheck.filter(isPresent);
    if (pokeUsers.length > 0) {
      const pokeUserCollectionIdentifiers = pokeUserCollection.map(pokeUserItem => this.getPokeUserIdentifier(pokeUserItem));
      const pokeUsersToAdd = pokeUsers.filter(pokeUserItem => {
        const pokeUserIdentifier = this.getPokeUserIdentifier(pokeUserItem);
        if (pokeUserCollectionIdentifiers.includes(pokeUserIdentifier)) {
          return false;
        }
        pokeUserCollectionIdentifiers.push(pokeUserIdentifier);
        return true;
      });
      return [...pokeUsersToAdd, ...pokeUserCollection];
    }
    return pokeUserCollection;
  }
}
