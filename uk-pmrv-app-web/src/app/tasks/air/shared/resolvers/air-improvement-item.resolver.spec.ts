import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AirTaskSharedModule } from '@tasks/air/shared/air-task-shared.module';
import { AirService } from '@tasks/air/shared/services/air.service';
import { mockAirApplicationSubmitPayload } from '@tasks/air/submit/testing/mock-air-application-submit-payload';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { AirImprovementItemResolver } from './air-improvement-item.resolver';

describe('AirImprovementItemResolver', () => {
  let resolver: AirImprovementItemResolver;
  let router: Router;
  let airService: Partial<jest.Mocked<AirService>>;

  const tasksService = mockClass(TasksService);

  beforeEach(() => {
    airService = {
      payload$: of(mockAirApplicationSubmitPayload),
    };

    TestBed.configureTestingModule({
      imports: [SharedModule, AirTaskSharedModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: AirService, useValue: airService },
      ],
    });

    resolver = TestBed.inject(AirImprovementItemResolver);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should resolve to an AirImprovement item', async () => {
    await expect(lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ id: '1' })))).resolves.toEqual({
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      parameter: 'Emission factor',
      sourceStreamReference: 'F1: Acetylene',
      tier: 'Tier 1',
      type: 'CALCULATION_CO2',
    });
  });

  it('should redirect to 404 page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValueOnce(true);
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const result = await lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ id: '6' })));
    expect(result).toEqual(undefined);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['error', '404']);
  });
});
