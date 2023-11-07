import { Location } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable, of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { RequestActionsService, RequestItemsService, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { AuthService } from '../../../core/services/auth.service';
import { StoreContextResolver } from '../../store-resolver/store-context.resolver';
import { PeerReviewDecisionGuard } from './peer-review-decision.guard';

describe('PeerReviewDecisionGuard', () => {
  let guard: PeerReviewDecisionGuard;
  let router: Router;
  let location: Location;
  const storeResolver = mockClass(StoreContextResolver);
  const tasksService = mockClass(TasksService);

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 237 };

  describe('for permit issuance', () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          PeerReviewDecisionGuard,
          { provide: TasksService, useValue: tasksService },
          { provide: StoreContextResolver, useValue: storeResolver },
        ],
      });

      storeResolver.getRequestId.mockReturnValue(of('1'));
      storeResolver.getRequestTaskType.mockReturnValue(of('PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW'));
      storeResolver.getAllowedRequestTaskActions.mockReturnValue(
        of(['PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION']),
      );

      guard = TestBed.inject(PeerReviewDecisionGuard);
      router = TestBed.inject(Router);
      location = TestBed.inject(Location);
      jest.spyOn(location, 'path').mockReturnValue('/permit-issuance/237/review/peer-review-decision');
    });

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should activate if prerequisites are met', async () => {
      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
      ).resolves.toEqual(true);
      expect(location.path).toHaveBeenCalledTimes(1);
    });

    it('should not activate if action is not allowed', async () => {
      storeResolver.getAllowedRequestTaskActions.mockReturnValue(of([]));

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`permit-issuance/${activatedRouteSnapshot.params.taskId}/review`));
      expect(location.path).toHaveBeenCalledTimes(1);
    });
  });

  describe('for permit notification', () => {
    beforeEach(() => {
      const keycloakService = mockClass(KeycloakService);
      const requestItemsService = mockClass(RequestItemsService);
      const requestActionsService = mockClass(RequestActionsService);
      const authService = mockClass(AuthService);

      TestBed.configureTestingModule({
        providers: [
          PeerReviewDecisionGuard,
          { provide: TasksService, useValue: tasksService },
          { provide: StoreContextResolver, useValue: storeResolver },
          { provide: KeycloakService, useValue: keycloakService },
          { provide: RequestItemsService, useValue: requestItemsService },
          { provide: RequestActionsService, useValue: requestActionsService },
          { provide: AuthService, useValue: authService },
        ],
      });

      storeResolver.getRequestTaskType.mockReturnValue(of('PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW'));
      storeResolver.getRequestId.mockReturnValue(of('1'));
      storeResolver.getAllowedRequestTaskActions.mockReturnValue(
        of(['PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION']),
      );

      guard = TestBed.inject(PeerReviewDecisionGuard);
      router = TestBed.inject(Router);
      location = TestBed.inject(Location);
      jest.spyOn(location, 'path').mockReturnValue('/tasks/237/permit-notification/peer-review-decision');
    });

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should activate if prerequisites are met', async () => {
      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
      ).resolves.toEqual(true);
      expect(location.path).toHaveBeenCalledTimes(1);
    });

    it('should not activate if action is not allowed', async () => {
      storeResolver.getAllowedRequestTaskActions.mockReturnValue(of([]));

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
      ).resolves.toEqual(
        router.parseUrl(`tasks/${activatedRouteSnapshot.params.taskId}/permit-notification/peer-review`),
      );
      expect(location.path).toHaveBeenCalledTimes(1);
    });
  });
});
