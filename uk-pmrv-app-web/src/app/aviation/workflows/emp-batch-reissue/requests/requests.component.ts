import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BehaviorSubject, distinctUntilChanged, shareReplay, switchMap } from 'rxjs';

import { RequestsService } from 'pmrv-api';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestsComponent {
  readonly pageSize = 30;
  currentPage$ = new BehaviorSubject<number>(1);

  response$ = this.currentPage$.pipe(
    distinctUntilChanged(),
    switchMap((currentPage) =>
      this.requestsService.getBatchReissueRequests('AVIATION', currentPage - 1, this.pageSize),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(private readonly requestsService: RequestsService) {}

  onCurrentPageChanged(page: number) {
    this.currentPage$.next(page);
  }
}
