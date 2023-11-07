import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { setStoreTask } from '../../testing/set-store-task';
import { EmissionSummariesSummaryGuard } from './emission-summaries-summary.guard';

describe('EmissionSummariesSummaryGuard', () => {
  let guard: EmissionSummariesSummaryGuard;
  let store: PermitApplicationStore<PermitApplicationState>;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        EmissionSummariesSummaryGuard,
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    store = TestBed.inject(PermitIssuanceStore);
    guard = TestBed.inject(EmissionSummariesSummaryGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow if summarized', async () => {
    store.setState(mockState);

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toEqual(
      router.parseUrl('/permit-issuance/1/emission-summaries'),
    );

    setStoreTask(
      'emissionSummaries',
      [
        ...store.permit.emissionSummaries,
        {
          sourceStream: '16236830126010.5957932377356623',
          emissionPoints: ['16363790610230.8369404469603225'],
          emissionSources: ['16245246343280.27155194483385103'],
          regulatedActivity: '16236817394240.1574963093314665',
        },
      ],
      [true],
    );

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toBeTruthy();
  });
});
