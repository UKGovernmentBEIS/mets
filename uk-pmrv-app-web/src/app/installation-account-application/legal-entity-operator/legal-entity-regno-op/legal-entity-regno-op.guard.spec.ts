import { TestBed } from '@angular/core/testing';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { LEGAL_ENTITY_FORM_OP } from '../../factories/legal-entity/legal-entity-form-op.factory';
import { LegalEntityRegnoOpGuard } from './legal-entity-regno-op.guard';

describe('LegalEntityRegnoOpGuard', () => {
  let guard: LegalEntityRegnoOpGuard;
  let router: jest.Mocked<Router>;
  let form: UntypedFormGroup;

  beforeEach(() => {
    router = {
      parseUrl: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    form = new UntypedFormBuilder().group({
      selectGroup: new UntypedFormBuilder().group({
        isNew: [null],
      }),
    });

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        LegalEntityRegnoOpGuard,
        { provide: Router, useValue: router },
        { provide: LEGAL_ENTITY_FORM_OP, useValue: form },
      ],
    });

    guard = TestBed.inject(LegalEntityRegnoOpGuard);
  });

  describe('canActivate', () => {
    it('should return true if isNew is true', () => {
      form.get('selectGroup').get('isNew').setValue(true);
      const route = new ActivatedRouteSnapshot();
      const state = { url: '/some/url' } as RouterStateSnapshot;

      const result = guard.canActivate(route, state);

      expect(result).toBe(true);
    });

    it('should return UrlTree if isNew is false', () => {
      form.get('selectGroup').get('isNew').setValue(false);
      const route = new ActivatedRouteSnapshot();
      route.url = [new UrlSegment('regno', {})];
      const state = { url: '/some/url/regno' } as RouterStateSnapshot;
      const expectedUrl = '/some';
      router.parseUrl.mockReturnValue(expectedUrl as unknown as UrlTree);

      const result = guard.canActivate(route, state);

      expect(result).toEqual(expectedUrl);
      expect(router.parseUrl).toHaveBeenCalledWith('/some');
    });
  });
});
