import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { NaceCodeSubCategoryGuard } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-sub-category/nace-code-sub-category.guard';
import { KeycloakService } from 'keycloak-angular';

describe('NaceCodeSubCategoryGuard', () => {
  let guard: NaceCodeSubCategoryGuard;
  let router: Router;

  const activatedRouteSnapshotMainActivitySet = new ActivatedRouteSnapshot();
  activatedRouteSnapshotMainActivitySet.queryParams = { mainActivity: 'a main activity' };

  const activatedRouteSnapshotMainActivityNotSet = new ActivatedRouteSnapshot();
  activatedRouteSnapshotMainActivityNotSet.params = { taskId: 23 };
  activatedRouteSnapshotMainActivityNotSet.queryParams = {};

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [HttpClient, HttpHandler, KeycloakService, NaceCodeSubCategoryGuard],
    });
    guard = TestBed.inject(NaceCodeSubCategoryGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow', async () => {
    expect(guard.canActivate(activatedRouteSnapshotMainActivitySet)).toEqual(true);
  });

  it('should not allow', async () => {
    expect(guard.canActivate(activatedRouteSnapshotMainActivityNotSet)).toEqual(
      router.parseUrl('tasks/23/aer/submit/nace-codes/add'),
    );
  });
});
