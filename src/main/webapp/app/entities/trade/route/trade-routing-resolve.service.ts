import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrade } from '../trade.model';
import { TradeService } from '../service/trade.service';

const tradeResolve = (route: ActivatedRouteSnapshot): Observable<null | ITrade> => {
  const id = route.params.id;
  if (id) {
    return inject(TradeService)
      .find(id)
      .pipe(
        mergeMap((trade: HttpResponse<ITrade>) => {
          if (trade.body) {
            return of(trade.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default tradeResolve;
