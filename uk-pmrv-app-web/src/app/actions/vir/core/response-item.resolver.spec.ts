import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockVirApplicationSubmittedRequestActionPayload } from '../submitted/testing/mock-aer-submitted';
import { ResponseItemResolver } from './response-item.resolver';
import { VirService } from './vir.service';

describe('ResponseItemResolver', () => {
  let resolver: ResponseItemResolver;
  let router: Router;
  let virService: Partial<jest.Mocked<VirService>>;

  const tasksService = mockClass(TasksService);

  beforeEach(() => {
    virService = {
      payload$: of(mockVirApplicationSubmittedRequestActionPayload),
    };

    TestBed.configureTestingModule({
      imports: [SharedModule, VirSharedModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: VirService, useValue: virService },
      ],
    });

    resolver = TestBed.inject(ResponseItemResolver);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should resolve to a verification data item', async () => {
    await expect(lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ id: 'A1' })))).resolves.toEqual({
      explanation: 'Test uncorrectedMisstatement A1',
      materialEffect: true,
      reference: 'A1',
    });
  });

  it('should redirect to 404 page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValueOnce(true);
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const result = await lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ id: 'A4' })));
    expect(result).toEqual(undefined);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['error', '404']);
  });
});
