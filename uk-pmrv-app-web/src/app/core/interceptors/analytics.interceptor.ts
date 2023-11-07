import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { logGoogleEvent } from '@core/analytics';
import { AuthStore } from '@core/store';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { AerService } from '@tasks/aer/core/aer.service';
import { HttpMethods } from 'keycloak-angular/lib/core/interfaces/keycloak-options';

type AllowedRoute = { method: HttpMethods; endpoint: string };

const taskActionRoute = {
  method: 'POST',
  endpoint: '/tasks/actions',
};
const userRegisterRoutes: AllowedRoute[] = [
  {
    method: 'POST',
    endpoint: '/operator-users/registration/register',
  },
  {
    method: 'PUT',
    endpoint: '/regulator-users/registration/enable-from-invitation',
  },
];

const accountCreateRoutes: AllowedRoute[] = [
  {
    endpoint: 'aviation/accounts',
    method: 'POST',
  },
  {
    endpoint: 'requests',
    method: 'POST',
  },
];

const allowedRoutes = [taskActionRoute, ...userRegisterRoutes, ...accountCreateRoutes];

@Injectable()
export class AnalyticsInterceptor implements HttpInterceptor {
  constructor(
    private authStore: AuthStore,
    private requestStore: RequestTaskStore,
    private permitStore: PermitIssuanceStore,
    private aerService: AerService,
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const allowedRoute = allowedRoutes.find((ar) => request.url.includes(ar.endpoint));
    if (!allowedRoute) return next.handle(request);
    if (request.url.includes(taskActionRoute.endpoint) && request.method === taskActionRoute.method) {
      this.handleTaskAction(request);
    } else if (userRegisterRoutes.some((r) => r.method === request.method && request.url.includes(r.endpoint))) {
      logGoogleEvent('USER_REGISTERED');
    } else if (accountCreateRoutes.some((r) => r.method === request.method && request.url.includes(r.endpoint))) {
      this.handleAccountCreate(request);
    }
    return next.handle(request);
  }

  handleTaskAction(request: HttpRequest<unknown>): void {
    let requestId: string;
    if (this.permitStore && this.permitStore.payload?.requestId) {
      requestId = this.permitStore.payload.requestId;
    } else if (this.requestStore.getValue().requestTaskItem?.requestInfo?.id) {
      requestId = this.requestStore.getValue().requestTaskItem?.requestInfo?.id;
    } else if (this.aerService.requestId) {
      // this works for account register as well because it uses common task store
      requestId = this.aerService.requestId;
    } else {
      console.warn('no request id. will not log ga event');
      return;
    }
    const body = request.body;
    const eventName = body['requestTaskActionType'];
    const requestTaskId = body['requestTaskId'];
    const scheme = this.authStore.getState().currentDomain;
    const role = this.authStore.getState().userState.roleType;

    logGoogleEvent(eventName, {
      requestTaskId,
      scheme,
      role,
      requestId,
    });
  }
  handleAccountCreate(request: HttpRequest<unknown>): void {
    if (request.url.includes('aviation')) {
      logGoogleEvent('AVIATION_ACCOUNT_OPENING', {
        scheme: 'AVIATION',
      });
    } else {
      if (request.body['requestCreateActionType'] === 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION')
        logGoogleEvent('INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION', {
          scheme: 'INSTALLATION',
        });
    }
  }
}
