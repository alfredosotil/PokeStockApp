import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICardSet } from '../card-set.model';
import { CardSetService } from '../service/card-set.service';

const cardSetResolve = (route: ActivatedRouteSnapshot): Observable<null | ICardSet> => {
  const id = route.params.id;
  if (id) {
    return inject(CardSetService)
      .find(id)
      .pipe(
        mergeMap((cardSet: HttpResponse<ICardSet>) => {
          if (cardSet.body) {
            return of(cardSet.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default cardSetResolve;
