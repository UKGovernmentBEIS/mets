import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { LegalEntitiesService } from 'pmrv-api';

import { legalEntityFormOpFactory } from '../../factories/legal-entity/legal-entity-form-op.factory';
import { LegalEntitySelectOpGuard } from './legal-entity-select-op.guard';

describe('LegalEntitySelectOpGuard', () => {
  let guard: LegalEntitySelectOpGuard;
  let router: Router;
  let legalEntitiesService: LegalEntitiesService;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('select', null)];
  const routerStateSnapshot = {
    url: '/installation-account/application/legal-entity-op/select',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LegalEntitiesService, LegalEntitySelectOpGuard, legalEntityFormOpFactory, UntypedFormBuilder],
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(LegalEntitySelectOpGuard);
    router = TestBed.inject(Router);
    legalEntitiesService = TestBed.inject(LegalEntitiesService);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to registration number if the user belongs to no legal entity', async () => {
    const navigateSpy = jest.spyOn(router, 'parseUrl').mockImplementation();
    jest.spyOn(legalEntitiesService, 'getCurrentUserLegalEntities').mockReturnValue(of([]));

    await lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot));

    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should return true if the user belongs to a legal entity', async () => {
    const navigateSpy = jest.spyOn(router, 'parseUrl').mockImplementation();
    jest.spyOn(legalEntitiesService, 'getCurrentUserLegalEntities').mockReturnValue(of([{ id: 1, name: 'test' }]));

    await lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot));

    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should resolve the legal entities', async () => {
    jest.spyOn(legalEntitiesService, 'getCurrentUserLegalEntities').mockReturnValue(of([]));
    expect(guard.resolve()).toBeUndefined();

    await lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot));

    expect(guard.resolve()).toEqual([]);
  });
});
