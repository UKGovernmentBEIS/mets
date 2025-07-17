import { HttpClientTestingModule } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { LegalEntitiesService } from 'pmrv-api';

import {
  LEGAL_ENTITY_FORM_REG,
  legalEntityFormRegFactory,
} from '../factories/legal-entity/legal-entity-form-reg.factory';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { LegalEntityDetailsGuard } from './legal-entity-details.guard';

describe('LegalEntityDetailsGuard', () => {
  let router: Router;
  let guard: LegalEntityDetailsGuard;
  let form: FormGroup;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('details', null)];
  const routerStateSnapshot = { url: '/installation-account/application/legal-entity/details/' } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        InstallationAccountApplicationStore,
        LegalEntitiesService,
        LegalEntityDetailsGuard,
        legalEntityFormRegFactory,
        UntypedFormBuilder,
      ],
    });
    router = TestBed.inject(Router);
    guard = TestBed.inject(LegalEntityDetailsGuard);
    form = TestBed.inject(LEGAL_ENTITY_FORM_REG);
  });

  afterEach(fakeAsync(() => flush()));

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if is new', fakeAsync(() => {
    form.get('selectGroup').get('isNew').setValue(true);

    expect(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot)).toEqual(true);
  }));

  it('should navigate away if not new', fakeAsync(() => {
    form.get('selectGroup').get('isNew').setValue(false);

    expect(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot)).toEqual(
      router.parseUrl('installation-account/application/legal-entity/select'),
    );
  }));
});
