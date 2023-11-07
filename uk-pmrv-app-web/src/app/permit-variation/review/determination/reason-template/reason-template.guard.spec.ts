import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitVariationStore } from '../../../store/permit-variation.store';
import { mockPermitVariationRegulatorLedPayload } from '../../../testing/mock';
import { ReasonTemplateGuard } from './reason-template.guard';

describe('ReasonTemplateGuard', () => {
  let guard: ReasonTemplateGuard;
  let router: Router;
  let store: PermitVariationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: mockPermitVariationRegulatorLedPayload.requestTaskId };
  activatedRouteSnapshot.data = {
    statusKey: 'determination',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(ReasonTemplateGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitVariationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if all wizard filled', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
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
    ).resolves.toEqual(router.parseUrl(`/permit-variation/237/review/determination/summary`));
  });

  it('should redirect to answers if determination not completed and all wizard filled', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
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

  it('should return true if not completed and reason template not exists', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPermitVariationRegulatorLedPayload,
      permitVariationDetails: {
        reason: 'reason',
        modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
      },
      permitVariationDetailsCompleted: true,
      determination: {
        reason: 'reason',
        activationDate: '2023-01-01',
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
