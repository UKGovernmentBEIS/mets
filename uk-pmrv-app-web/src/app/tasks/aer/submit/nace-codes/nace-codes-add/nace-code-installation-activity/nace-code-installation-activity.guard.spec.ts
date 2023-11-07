import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { NaceCodeInstallationActivityGuard } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-installation-activity/nace-code-installation-activity.guard';
import { KeycloakService } from 'keycloak-angular';

describe('NaceCodeInstallationActivityGuard', () => {
  let guard: NaceCodeInstallationActivityGuard;
  let router: Router;

  const activatedRouteSnapshotSubcategorySet = new ActivatedRouteSnapshot();
  activatedRouteSnapshotSubcategorySet.queryParams = { subCategory: 'a subcategory' };

  const activatedRouteSnapshotSubcategoryNotSet = new ActivatedRouteSnapshot();
  activatedRouteSnapshotSubcategoryNotSet.params = { taskId: 23 };
  activatedRouteSnapshotSubcategoryNotSet.queryParams = {};

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [HttpClient, HttpHandler, KeycloakService, NaceCodeInstallationActivityGuard],
    });
    guard = TestBed.inject(NaceCodeInstallationActivityGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow', async () => {
    expect(guard.canActivate(activatedRouteSnapshotSubcategorySet)).toEqual(true);
  });

  it('should not allow', async () => {
    expect(guard.canActivate(activatedRouteSnapshotSubcategoryNotSet)).toEqual(
      router.parseUrl('tasks/23/aer/submit/nace-codes/add'),
    );
  });
});
