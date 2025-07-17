import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';

import { of } from 'rxjs';

import { LegalEntitiesService, LegalEntityInfoDTO } from 'pmrv-api';

import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { LegalEntitySelectGuard } from './legal-entity-select.guard';

@Injectable()
class TestLegalEntitySelectGuard extends LegalEntitySelectGuard {
  constructor(
    form: UntypedFormGroup,
    legalEntitiesService: LegalEntitiesService,
    router: Router,
    store: InstallationAccountApplicationStore,
  ) {
    super(form, legalEntitiesService, router, store);
  }

  getRedirectUrl(route: ActivatedRouteSnapshot, state: RouterStateSnapshot, redirectUrlPart: string): string {
    return super.getRedirectUrl(route, state, redirectUrlPart);
  }
}

describe('LegalEntitySelectGuard', () => {
  let guard: TestLegalEntitySelectGuard;
  let router: jest.Mocked<Router>;
  let legalEntitiesService: jest.Mocked<LegalEntitiesService>;
  let store: jest.Mocked<InstallationAccountApplicationStore>;
  let form: UntypedFormGroup;

  beforeEach(() => {
    router = {
      parseUrl: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    legalEntitiesService = {
      getCurrentUserLegalEntities: jest.fn(),
    } as unknown as jest.Mocked<LegalEntitiesService>;

    store = {
      updateTask: jest.fn(),
    } as unknown as jest.Mocked<InstallationAccountApplicationStore>;

    form = new UntypedFormBuilder().group({
      selectGroup: new UntypedFormBuilder().group({
        isNew: [null],
        id: [null],
      }),
    });

    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: LegalEntitiesService, useValue: legalEntitiesService },
        { provide: Router, useValue: router },
        { provide: InstallationAccountApplicationStore, useValue: store },
        { provide: UntypedFormGroup, useValue: form },
        TestLegalEntitySelectGuard,
      ],
    });

    guard = TestBed.inject(TestLegalEntitySelectGuard);
  });

  describe('canActivate', () => {
    it('should redirect to the specified URL if no legal entities exist', (done) => {
      const route = new ActivatedRouteSnapshot();
      route.url = [new UrlSegment('select', {})];
      const state = { url: '/some/url' } as RouterStateSnapshot;
      const redirectUrlPart = 'details';
      const expectedUrl = '/some/details';

      legalEntitiesService.getCurrentUserLegalEntities.mockReturnValue(of([]));
      router.parseUrl.mockReturnValue(expectedUrl as unknown as UrlTree);

      guard.canActivate(route, state, redirectUrlPart).subscribe((result) => {
        expect(result).toEqual(expectedUrl);
        expect(form.get('selectGroup').get('isNew').value).toBe(true);
        expect(store.updateTask).toHaveBeenCalledWith(ApplicationSectionType.legalEntity, {
          selectGroup: { isNew: true },
        });
        done();
      });
    });

    it('should return true if legal entities exist', (done) => {
      const route = new ActivatedRouteSnapshot();
      const state = { url: '/some/url' } as RouterStateSnapshot;
      const redirectUrlPart = 'details';
      const legalEntities: LegalEntityInfoDTO[] = [{ id: 1, name: 'Entity 1' }];

      legalEntitiesService.getCurrentUserLegalEntities.mockReturnValue(of(legalEntities));

      guard.canActivate(route, state, redirectUrlPart).subscribe((result) => {
        expect(result).toBe(true);
        done();
      });
    });
  });

  describe('resolve', () => {
    it('should return the cached legal entities', () => {
      const legalEntities: LegalEntityInfoDTO[] = [{ id: 1, name: 'Entity 1' }];
      (guard as any).legalEntities = legalEntities;

      const result = guard.resolve();
      expect(result).toEqual(legalEntities);
    });
  });
});
