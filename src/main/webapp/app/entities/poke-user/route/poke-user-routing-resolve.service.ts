import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPokeUser } from '../poke-user.model';
import { PokeUserService } from '../service/poke-user.service';

const pokeUserResolve = (route: ActivatedRouteSnapshot): Observable<null | IPokeUser> => {
  const id = route.params.id;
  if (id) {
    return inject(PokeUserService)
      .find(id)
      .pipe(
        mergeMap((pokeUser: HttpResponse<IPokeUser>) => {
          if (pokeUser.body) {
            return of(pokeUser.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default pokeUserResolve;
