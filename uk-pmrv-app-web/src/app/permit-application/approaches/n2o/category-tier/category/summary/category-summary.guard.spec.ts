import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../../testing/mock-state';
import { CategorySummaryGuard } from './category-summary.guard';

describe('CategorySummaryGuard', () => {
  let guard: CategorySummaryGuard;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        CategorySummaryGuard,
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    store = TestBed.inject(PermitApplicationStore);
    guard = TestBed.inject(CategorySummaryGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate source stream category summary', async () => {
    store.setState(
      mockStateBuild(mockState, {
        MEASUREMENT_N2O_Category: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should activate source stream category summary', async () => {
    store.setState(
      mockStateBuild(mockState, {
        MEASUREMENT_N2O_Category: [true],
        emissionPoints: [true],
        emissionSources: [false],
        sourceStreams: [true],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should activate source stream category summary', async () => {
    store.setState(
      mockStateBuild(mockState, {
        MEASUREMENT_N2O_Category: [true],
        emissionPoints: [false],
        emissionSources: [true],
        sourceStreams: [true],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should activate source stream category summary', async () => {
    store.setState(
      mockStateBuild(mockState, {
        MEASUREMENT_N2O_Category: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [false],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should redirect to MEASUREMENT_N2O source stream category view', async () => {
    store.setState(mockStateBuild(mockState));

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-issuance/237/nitrous-oxide/category-tier/0/category'));
  });
});
