import { Location } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { SharedStore } from '@shared/store/shared.store';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../../testing';
import { StoreContextResolver } from '../../../store-resolver/store-context.resolver';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let guard: AnswersGuard;
  let sharedStore: SharedStore;
  let router: Router;
  let location: Location;

  const storeResolver = mockClass(StoreContextResolver);

  const route = new ActivatedRouteSnapshotStub({ taskId: '237' });

  describe('for permit issuance', () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [{ provide: StoreContextResolver, useValue: storeResolver }],
      });

      sharedStore = TestBed.inject(SharedStore);
      router = TestBed.inject(Router);
      location = TestBed.inject(Location);
      guard = TestBed.inject(AnswersGuard);

      jest.spyOn(location, 'path').mockReturnValue('/permit-issuance/237/review/peer-review-decision/answers');
    });

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should not activate if not decision set', async () => {
      await expect(firstValueFrom(guard.canActivate(route) as Observable<boolean | UrlTree>)).resolves.toEqual(
        router.parseUrl(`permit-issuance/237/review/peer-review-decision`),
      );
      expect(location.path).toHaveBeenCalledTimes(1);
    });

    it('should activate if prerequisites are met', async () => {
      sharedStore.setState({
        ...sharedStore.getState(),
        peerReviewDecision: {
          type: 'AGREE',
          notes: 'I agree',
        },
      });
      await expect(firstValueFrom(guard.canActivate(route) as Observable<boolean | UrlTree>)).resolves.toEqual(true);
      expect(location.path).toHaveBeenCalledTimes(1);
    });
  });

  describe('for permit notification', () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [{ provide: StoreContextResolver, useValue: storeResolver }],
      });

      sharedStore = TestBed.inject(SharedStore);
      router = TestBed.inject(Router);
      location = TestBed.inject(Location);
      guard = TestBed.inject(AnswersGuard);

      jest.spyOn(location, 'path').mockReturnValue('/tasks/237/permit-notification/peer-review/decision/answers');
    });

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should not activate if not decision set yet', async () => {
      await expect(firstValueFrom(guard.canActivate(route) as Observable<boolean | UrlTree>)).resolves.toEqual(
        router.parseUrl(`tasks/237/permit-notification/peer-review/decision`),
      );
      expect(location.path).toHaveBeenCalledTimes(1);
    });

    it('should activate if decision set', async () => {
      sharedStore.setState({
        ...sharedStore.getState(),
        peerReviewDecision: {
          type: 'AGREE',
          notes: 'I agree',
        },
      });
      await expect(firstValueFrom(guard.canActivate(route) as Observable<boolean | UrlTree>)).resolves.toEqual(true);
      expect(location.path).toHaveBeenCalledTimes(1);
    });
  });
});
