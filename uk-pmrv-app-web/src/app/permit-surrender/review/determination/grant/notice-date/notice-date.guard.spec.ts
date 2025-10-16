import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, UrlSegment, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitSurrenderReviewDeterminationGrant, TasksService } from 'pmrv-api';

import { MockType } from '../../../../../../testing';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { NoticeDateGuard } from './notice-date.guard';

describe('NoticeDateGuard', () => {
  let router: Router;
  let guard: NoticeDateGuard;
  let store: PermitSurrenderStore;

  const tasksService: MockType<TasksService> = {};

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('notice-date', null)];
  activatedRouteSnapshot.params = { taskId: mockTaskState.requestTaskId };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter([]), { provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(NoticeDateGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitSurrenderStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to answers when status is not completed and wizard completed', () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: false,
      reviewDetermination: {
        type: 'GRANTED',
        reason: 'reason',
        stopDate: '2012-12-13',
        noticeDate: '2012-12-13',
        reportRequired: false,
        alrRequired: false,
        allowancesSurrenderRequired: false,
      } as PermitSurrenderReviewDeterminationGrant,
    });

    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/review/determination/grant/answers`),
    );
  });

  it('should return true when wizard and status are not completed and previous steps are filled', () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: false,
      reviewDetermination: {
        type: 'GRANTED',
        reason: 'reason',
        stopDate: '2012-12-13',
        noticeDate: undefined,
      } as any,
    });

    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      true,
    );
  });
});
