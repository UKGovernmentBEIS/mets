import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { mockWasteQdrSubmitPayload, wasteQdrSubmitMockState } from '../../test/submit-mock';
import { SendReportGuard } from './send-report-guard';

describe('SendReportGuard', () => {
  let store: CommonTasksStore;
  let guard: SendReportGuard;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ItemNamePipe, provideHttpClient(withInterceptorsFromDi())],
    });
    guard = TestBed.inject(SendReportGuard);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    store.setState({
      ...wasteQdrSubmitMockState,
      requestTaskItem: {
        ...wasteQdrSubmitMockState.requestTaskItem,
        requestTask: {
          ...wasteQdrSubmitMockState.requestTaskItem.requestTask,
          payload: {
            ...mockWasteQdrSubmitPayload,

            wasteQDRSectionsCompleted: {
              qdr: true,
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
