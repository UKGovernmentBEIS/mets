import { ActivatedRoute, ActivatedRouteSnapshot, Router } from '@angular/router';

export function getActiveRoute(router: Router, snapshot: false): ActivatedRoute;
export function getActiveRoute(router: Router, snapshot: true): ActivatedRouteSnapshot;
export function getActiveRoute(router: Router, snapshot: boolean): ActivatedRoute | ActivatedRouteSnapshot {
  let activeRoute = snapshot ? router.routerState.snapshot.root : router.routerState.root;
  while (activeRoute.firstChild) {
    activeRoute = activeRoute.firstChild;
  }

  return activeRoute;
}
