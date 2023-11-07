import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  PRIMARY_OUTLET,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { StoreContextResolver } from '../store-resolver/store-context.resolver';
import { UrlRequestType, urlRequestTypes } from '../types/url-request-type';

@Injectable()
export class PaymentCompletedGuard implements CanActivate {
  constructor(private readonly storeResolver: StoreContextResolver, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): Observable<true | UrlTree> {
    const tree = this.router.parseUrl(routerState.url);
    const segmentGroup = tree.root.children[PRIMARY_OUTLET];
    const segment = segmentGroup.segments;

    const lastSegment = segment
      .filter((index) => urlRequestTypes.some((type) => index.path.includes(type)))
      .slice(0, 1);

    const isAviation = lastSegment[0].path === 'aviation';

    const aviationRedirectStringUrl = isAviation ? `/${segment[0].path}/${segment[1].path}/${segment[2].path}` : null;

    const redirectUrlPath = aviationRedirectStringUrl
      ? aviationRedirectStringUrl
      : `/${lastSegment[0].path}/${route.paramMap.get('taskId')}`;

    const redirectUrl =
      lastSegment[0].path === 'permit-issuance' ||
      lastSegment[0].path === 'permit-variation' ||
      lastSegment[0].path === 'permit-transfer' ||
      lastSegment[0].path === 'permit-surrender'
        ? this.router.parseUrl(redirectUrlPath.concat(`/review/payment-not-completed`))
        : this.router.parseUrl(redirectUrlPath.concat('/payment-not-completed'));

    const store = this.storeResolver.getStore(lastSegment[0].path as UrlRequestType);

    return store.pipe(
      first(),
      map((state) => {
        const paymentCompleted = isAviation
          ? state.requestTaskItem?.requestInfo?.paymentCompleted
          : !!state.paymentCompleted;

        return !(store as any).isPaymentRequired || !!paymentCompleted || redirectUrl;
      }),
    );
  }
}
