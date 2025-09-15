import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMarketPrice } from '../market-price.model';
import { MarketPriceService } from '../service/market-price.service';

const marketPriceResolve = (route: ActivatedRouteSnapshot): Observable<null | IMarketPrice> => {
  const id = route.params.id;
  if (id) {
    return inject(MarketPriceService)
      .find(id)
      .pipe(
        mergeMap((marketPrice: HttpResponse<IMarketPrice>) => {
          if (marketPrice.body) {
            return of(marketPrice.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default marketPriceResolve;
