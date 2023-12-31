import { Route } from '@angular/router';

import { RouteBacklink } from '@core/navigation/backlink/backlink.interface';
import { RouteBreadcrumb } from '@core/navigation/breadcrumbs';

import { StatusKey } from './permit-surrender.type';

export interface PermitSurrenderRoute extends Route {
  data?: PermitSurrenderRouteData;
  children?: PermitSurrenderRoute[];
}

export interface PermitSurrenderRouteData {
  pageTitle?: string;
  statusKey?: StatusKey;
  breadcrumb?: RouteBreadcrumb;
  backlink?: RouteBacklink;
}
