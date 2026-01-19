import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable, of } from 'rxjs';

import { mockClass, MockType } from '@testing';

import { InstallationAccountViewService } from 'pmrv-api';

import { mockedAccountPermit } from '../../../../accounts/testing/mock-data';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
} from '../../../../permit-variation/testing/mock';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockReviewState, mockReviewStateBuild } from '../../../testing/mock-state';
import { ReasonGuard } from './reason.guard';

describe('ReasonGuard', () => {
  let guard: ReasonGuard;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let accountViewService: MockType<InstallationAccountViewService>;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: mockReviewState.requestTaskId };
  activatedRouteSnapshot.data = {
    statusKey: 'determination',
  };

  describe('permit issuance', () => {
    accountViewService = mockClass(InstallationAccountViewService);
    accountViewService.getInstallationAccountById.mockReturnValue(
      of({
        accountPermitDto: {
          ...mockedAccountPermit,
          account: { ...mockedAccountPermit.account, emissionTradingScheme: 'UK_ETS_INSTALLATIONS' },
        },
      }),
    );
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          provideRouter([]),
          provideHttpClient(),
          ReasonGuard,
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
          { provide: InstallationAccountViewService, useValue: accountViewService },
        ],
      });
      guard = TestBed.inject(ReasonGuard);
      router = TestBed.inject(Router);
      store = TestBed.inject(PermitApplicationStore);
    });

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should activate if task is changing', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
      await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
    });

    it('should activate if task is in progress and not changing', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState(
        mockReviewStateBuild({
          type: 'GRANTED',
        }),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to determination if type is missing', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState(
        mockReviewStateBuild({
          determination: false,
        }),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-issuance/237/review/determination`));
    });

    it('should redirect to answers if task needs review and not changing ', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState(
        mockReviewStateBuild(
          {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: '1-1-2030',
            firstYearOfReportingObligation: 2022,
          },
          {
            determination: false,
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-issuance/237/review/determination/answers`));
    });

    it('should redirect to summary if task is complete and not changing', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState(
        mockReviewStateBuild(
          {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: '1-1-2030',
          },
          {
            determination: true,
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-issuance/237/review/determination/summary`));
    });
  });

  describe('permit variation', () => {
    accountViewService = mockClass(InstallationAccountViewService);
    accountViewService.getInstallationAccountById.mockReturnValue(
      of({
        accountPermitDto: {
          ...mockedAccountPermit,
          account: { ...mockedAccountPermit.account, emissionTradingScheme: 'UK_ETS_INSTALLATIONS' },
        },
      }),
    );

    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          provideRouter([]),
          provideHttpClient(),
          ReasonGuard,
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
          { provide: InstallationAccountViewService, useValue: accountViewService },
        ],
      });
      guard = TestBed.inject(ReasonGuard);
      router = TestBed.inject(Router);
      store = TestBed.inject(PermitApplicationStore);
    });

    it('should redirect to answers if variation task and granted and not completed and all wizard filled', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        determination: {
          type: 'GRANTED',
          reason: 'reason',
          activationDate: '1-1-2030',
          logChanges: 'klklkllkllk',
        },
        reviewSectionsCompleted: { determination: false },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-variation/237/review/determination/answers`));
    });

    it('should return true if variation task and granted and not completed and reason not exists', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        determination: {
          type: 'GRANTED',
        },
        reviewSectionsCompleted: { determination: false },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to answers if variation regulator led task and not completed and all wizard filled', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      (store as PermitVariationStore).setState({
        ...mockPermitVariationRegulatorLedPayload,
        permitVariationDetails: {
          reason: 'reason',
          modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
        },
        permitVariationDetailsCompleted: true,
        determination: {
          reason: 'reason',
          activationDate: '2023-01-01',
          reasonTemplate: 'WHERE_OPERATOR_FAILED_TO_APPLY_IN_ACCORDANCE_WITH_CONDITIONS',
          logChanges: 'logChanges',
        },
        reviewSectionsCompleted: { determination: false },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-variation/237/review/determination/answers`));
    });
  });
});
