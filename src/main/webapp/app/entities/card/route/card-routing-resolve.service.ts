import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICard } from '../card.model';
import { CardService } from '../service/card.service';

const cardResolve = (route: ActivatedRouteSnapshot): Observable<null | ICard> => {
  const id = route.params.id;
  if (id) {
    return inject(CardService)
      .find(id)
      .pipe(
        mergeMap((card: HttpResponse<ICard>) => {
          if (card.body) {
            return of(card.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default cardResolve;
