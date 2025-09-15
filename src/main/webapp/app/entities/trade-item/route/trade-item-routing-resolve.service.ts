import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITradeItem } from '../trade-item.model';
import { TradeItemService } from '../service/trade-item.service';

const tradeItemResolve = (route: ActivatedRouteSnapshot): Observable<null | ITradeItem> => {
  const id = route.params.id;
  if (id) {
    return inject(TradeItemService)
      .find(id)
      .pipe(
        mergeMap((tradeItem: HttpResponse<ITradeItem>) => {
          if (tradeItem.body) {
            return of(tradeItem.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default tradeItemResolve;
