import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, PRIMARY_OUTLET, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { StoreContextResolver } from '../store-resolver/store-context.resolver';
import { UrlRequestType, urlRequestTypes } from '../types/url-request-type';

@Injectable()
export class PaymentCompletedGuard {
  constructor(
    private readonly storeResolver: StoreContextResolver,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): Observable<true | UrlTree> {
    const tree = this.router.parseUrl(routerState.url);
    const segmentGroup = tree.root.children[PRIMARY_OUTLET];
    const segment = segmentGroup.segments;

    const lastSegment = segment
      .filter((index) => urlRequestTypes.some((type) => index.path.includes(type)))
      .slice(0, 1);

    const isAviation = lastSegment[0].path === 'aviation';
    const isTasksWorkflow = ['hseti', 'ner'].includes(lastSegment[0].path);
    const isWorkflow = segment.filter((seg) => seg.path === 'workflows').length > 0;
    let aviationRedirectStringUrl = isAviation ? `/${segment[0].path}/${segment[1].path}/${segment[2].path}` : null;
    let redirectUrlPath = aviationRedirectStringUrl
      ? aviationRedirectStringUrl
      : isTasksWorkflow
        ? `/tasks/${route.paramMap.get('taskId')}/${lastSegment[0].path}`
        : `/${lastSegment[0].path}/${route.paramMap.get('taskId')}`;

    if (isWorkflow) {
      aviationRedirectStringUrl = isAviation
        ? `/${segment[0].path}/${segment[1].path}/${segment[2].path}/${segment[3].path}/${segment[4].path}/${segment[5].path}/${segment[6].path}`
        : null;

      redirectUrlPath = aviationRedirectStringUrl
        ? aviationRedirectStringUrl
        : isTasksWorkflow
          ? `/${segment[0].path}/${segment[1].path}/${segment[2].path}/${segment[3].path}/tasks/${route.paramMap.get('taskId')}/${lastSegment[0].path}`
          : `/${segment[0].path}/${segment[1].path}/${segment[2].path}/${segment[3].path}/${lastSegment[0].path}/${route.paramMap.get('taskId')}`;
    }

    const redirectUrl =
      lastSegment[0].path === 'permit-issuance' ||
      lastSegment[0].path === 'permit-variation' ||
      lastSegment[0].path === 'permit-transfer' ||
      lastSegment[0].path === 'permit-surrender' ||
      lastSegment[0].path === 'ner'
        ? this.router.parseUrl(redirectUrlPath.concat(`/review/payment-not-completed`))
        : this.router.parseUrl(redirectUrlPath.concat('/payment-not-completed'));

    const store = this.storeResolver.getStore(lastSegment[0].path as UrlRequestType);

    return store.pipe(
      first(),
      map((state) => {
        const paymentCompleted =
          isAviation || isTasksWorkflow
            ? state.requestTaskItem?.requestInfo?.paymentCompleted
            : !!state.paymentCompleted;
        return isTasksWorkflow
          ? !!paymentCompleted || redirectUrl
          : !(store as any).isPaymentRequired || !!paymentCompleted || redirectUrl;
      }),
    );
  }
}
