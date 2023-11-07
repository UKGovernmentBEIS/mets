import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { SourceStreamDetailsGuard } from './source-stream-details.guard';

describe('SourceStreamDetailsGuard', () => {
  let guard: SourceStreamDetailsGuard;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        SourceStreamDetailsGuard,
        { provide: TasksService, useValue: {} },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });

    guard = TestBed.inject(SourceStreamDetailsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitIssuanceStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to task list if no data available', async () => {
    store.setState(mockState);

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 23 })))).resolves.toEqual(
      router.parseUrl('/permit-issuance/23/source-streams/summary'),
    );

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            streamId: 'nonexisitng',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/permit-issuance/23/source-streams/summary'));
  });

  it('should activate if data exist', async () => {
    store.setState(mockState);

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            streamId: mockPermitApplyPayload.permit.sourceStreams[0].id,
          }),
        ),
      ),
    ).resolves.toBeTruthy();

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            streamId: 'notexisting',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/permit-issuance/23/source-streams/summary'));
  });
});
