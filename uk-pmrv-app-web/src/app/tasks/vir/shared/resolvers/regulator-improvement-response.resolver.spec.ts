import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { mockVirApplicationRespondPayload } from '@tasks/vir/comments-response/testing/mock-vir-application-respond-payload';
import { VirTaskCommentsResponseModule } from '@tasks/vir/comments-response/vir-task-comments-response.module';
import { VirService } from '@tasks/vir/core/vir.service';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { RegulatorImprovementResponseResolver } from './regulator-improvement-response.resolver';

describe('RegulatorResponseResolver', () => {
  let resolver: RegulatorImprovementResponseResolver;
  let router: Router;
  let virService: Partial<jest.Mocked<VirService>>;

  const tasksService = mockClass(TasksService);

  beforeEach(() => {
    virService = {
      payload$: of(mockVirApplicationRespondPayload),
    };

    TestBed.configureTestingModule({
      imports: [SharedModule, VirTaskCommentsResponseModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: VirService, useValue: virService },
      ],
    });

    resolver = TestBed.inject(RegulatorImprovementResponseResolver);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should resolve to a reference', async () => {
    await expect(lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ id: 'A1' })))).resolves.toEqual('A1');
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
