import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { UserStateDTO } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../testing';
import { AuthStore } from '../../core/store';
import { RoleTypeGuard } from './role-type.guard';

describe('RoleTypeGuard', () => {
  let guard: RoleTypeGuard;
  let router: Router;
  let store: AuthStore;

  function setUserState(roleType: UserStateDTO['roleType']) {
    store.setUserState({
      ...store.getState().userState,
      roleType,
    });
  }

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [RouterTestingModule] });
    store = TestBed.inject(AuthStore);
    guard = TestBed.inject(RoleTypeGuard);
    router = TestBed.inject(Router);
  });

  describe('for operator user', () => {
    beforeEach(() => setUserState('OPERATOR'));

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should activate when guard guards operators role type', async () => {
      await expect(
        lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({}, {}, { roleTypeGuards: 'OPERATOR' }))),
      ).resolves.toBeTruthy();
    });

    it('should not activate when guard guards regulator role type', async () => {
      await expect(
        lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({}, {}, { roleTypeGuards: 'REGULATOR' }))),
      ).resolves.toEqual(router.parseUrl('/landing'));
    });
  });
});
