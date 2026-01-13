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
import { mockReviewStateBuild } from '../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let guard: AnswersGuard;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let accountViewService: MockType<InstallationAccountViewService>;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 276 };
  activatedRouteSnapshot.data = {
    statusKey: 'determination',
  };

  describe('permit issuance', () => {
    accountViewService = mockClass(InstallationAccountViewService);
    accountViewService.getInstallationAccountById.mockReturnValue(
      of({
        ...mockedAccountPermit,
        account: { ...mockedAccountPermit.account, emissionTradingScheme: 'UK_ETS_INSTALLATIONS' },
      }),
    );
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          provideRouter([]),
          provideHttpClient(),
          AnswersGuard,
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
          { provide: InstallationAccountViewService, useValue: accountViewService },
        ],
      });
      guard = TestBed.inject(AnswersGuard);
      router = TestBed.inject(Router);
      store = TestBed.inject(PermitApplicationStore);
    });

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should activate if determination is filled and task not completed ', async () => {
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
      ).resolves.toEqual(true);
    });

    it('should redirect to determination if determination is not filled', async () => {
      store.setState(
        mockReviewStateBuild(
          {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: undefined,
          },
          {
            determination: false,
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-issuance/276/review/determination`));
    });

    it('should redirect to summary if task is complete and not changing', async () => {
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
      ).resolves.toEqual(router.parseUrl(`/permit-issuance/276/review/determination/summary`));
    });
  });

  describe('permit variation', () => {
    accountViewService = mockClass(InstallationAccountViewService);
    accountViewService.getInstallationAccountById.mockReturnValue(
      of({
        ...mockedAccountPermit,
        account: { ...mockedAccountPermit.account, emissionTradingScheme: 'UK_ETS_INSTALLATIONS' },
      }),
    );
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          provideRouter([]),
          provideHttpClient(),
          AnswersGuard,
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
          { provide: InstallationAccountViewService, useValue: accountViewService },
        ],
      });
      guard = TestBed.inject(AnswersGuard);
      router = TestBed.inject(Router);
      store = TestBed.inject(PermitApplicationStore);
    });

    it('should return true if variation task and granted and determination not completed yet', async () => {
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
      ).resolves.toEqual(true);
    });

    it('should return true if variation regulator led task and determination not completed yet', async () => {
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
      ).resolves.toEqual(true);
    });

    it('should redirect to summary if variation regulator led task and determination completed', async () => {
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
        reviewSectionsCompleted: { determination: true },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-variation/276/review/determination/summary`));
    });
  });
});
