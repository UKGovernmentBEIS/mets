import { Route } from '@angular/router';

import { RouteBacklink } from '@core/navigation/backlink/backlink.interface';
import { RouteBreadcrumb } from '@core/navigation/breadcrumbs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { AboutVariationGroupKey } from '../permit-variation/variation-types';
import { PermitTaskType, StatusKey } from './shared/types/permit-task.type';

export interface PermitRoute extends Route {
  data?: PermitRouteData;
  children?: PermitRoute[];
}

export interface PermitRouteData {
  pageTitle?: string;
  permitTask?: PermitTaskType;
  statusKey?: StatusKey;
  taskKey?: string;
  groupKey?: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'] | AboutVariationGroupKey;
  breadcrumb?: RouteBreadcrumb;
  backlink?: RouteBacklink;
}
