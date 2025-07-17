import { TestBed } from '@angular/core/testing';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { LEGAL_ENTITY_FORM_OP } from '../../factories/legal-entity/legal-entity-form-op.factory';
import { LegalEntityDetailsOpGuard } from './legal-entity-details-op.guard';

describe('LegalEntityDetailsOpGuard', () => {
  let guard: LegalEntityDetailsOpGuard;
  let router: jest.Mocked<Router>;
  let form: UntypedFormGroup;

  beforeEach(() => {
    router = {
      parseUrl: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    form = new UntypedFormBuilder().group({
      referenceNumberGroup: new UntypedFormBuilder().group({
        isEntityRegistered: [null],
      }),
    });

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        LegalEntityDetailsOpGuard,
        { provide: Router, useValue: router },
        { provide: LEGAL_ENTITY_FORM_OP, useValue: form },
      ],
    });

    guard = TestBed.inject(LegalEntityDetailsOpGuard);
  });

  describe('canActivate', () => {
    it('should return true if isEntityRegistered is not null', () => {
      form.get('referenceNumberGroup').get('isEntityRegistered').setValue(true);
      const route = new ActivatedRouteSnapshot();
      const state = { url: '/some/url' } as RouterStateSnapshot;

      const result = guard.canActivate(route, state);

      expect(result).toBe(true);
    });

    it('should return UrlTree if isEntityRegistered is null', () => {
      form.get('referenceNumberGroup').get('isEntityRegistered').setValue(null);
      const route = new ActivatedRouteSnapshot();
      route.url = [new UrlSegment('details', {})];
      const state = { url: '/some/url/details' } as RouterStateSnapshot;
      const expectedUrl = '/some';
      router.parseUrl.mockReturnValue(expectedUrl as unknown as UrlTree);

      const result = guard.canActivate(route, state);

      expect(result).toEqual(expectedUrl);
      expect(router.parseUrl).toHaveBeenCalledWith('/some');
    });
  });
});
