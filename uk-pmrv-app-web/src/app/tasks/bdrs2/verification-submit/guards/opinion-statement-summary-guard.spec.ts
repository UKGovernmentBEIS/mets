import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { TasksService } from 'pmrv-api';

import { mockStateBuild } from '../testing/mock-state';
import { OpinionStatementSummaryGuard } from './opinion-statement-summary-guard';

describe('OpinionStatementSummaryGuard', () => {
  let guard: OpinionStatementSummaryGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 1 };
  const routerStateSnapshot = {
    url: '/tasks/1/bdrs2/verification-submit/opinion-statement/summary',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BdrS2TaskSharedModule],
      providers: [OpinionStatementSummaryGuard, BdrS2Service, ItemNamePipe, { provide: TasksService, useValue: {} }],
    });

    guard = TestBed.inject(OpinionStatementSummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if data model is complete', async () => {
    store.setState(
      mockStateBuild({
        opinionStatement: {
          opinionStatementFiles: ['28e34397-8308-409c-ad0a-f52a0101f1cc'],
          notes: 'test',
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should activate if  data model is wrong', async () => {
    store.setState({
      ...mockStateBuild({
        opinionStatement: {
          opinionStatementFiles: [],
        },
      }),
      isEditable: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/bdrs2/verification-submit/opinion-statement'));
  });
});
