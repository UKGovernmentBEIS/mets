import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment } from '@angular/router';

import { firstValueFrom } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { mockStateBuild } from '../testing/mock-state';
import { BaselineStepGuard } from './baseline-step-guard';

describe('BaselineStepGuard', () => {
  let guard: BaselineStepGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('', null)];
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BdrService, ItemNamePipe, provideHttpClient(withInterceptorsFromDi()), CapitalizeFirstPipe],
    });

    guard = TestBed.inject(BaselineStepGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should  activate if data model is wrong', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    store.setState(
      mockStateBuild({
        bdr: {
          statusApplicationType: 'HSE',
        },
        bdrSectionsCompleted: {
          baseline: false,
        },
      }),
    );

    expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should go to summary  if  data model is complete', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    store.setState({
      ...mockStateBuild({
        bdr: {
          isApplicationForFreeAllocation: true,
          statusApplicationType: 'HSE',
          infoIsCorrectChecked: true,
          hasMmp: false,
          files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
        },
        bdrSectionsCompleted: {
          baseline: false,
        },
      }),
    });

    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot))).resolves.toEqual(
      router.parseUrl('/tasks/1/bdr/submit/baseline/summary'),
    );
  });
});
