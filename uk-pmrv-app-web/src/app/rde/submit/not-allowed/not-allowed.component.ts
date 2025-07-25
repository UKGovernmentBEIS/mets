import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AVIATION_REQUEST_TYPES } from '@shared/utils/request.utils';

import { ItemDTO, RequestItemsService } from 'pmrv-api';

import { waitForRfiRdeTypes } from '../../core/rde';
import { RdeStore } from '../../store/rde.store';

@Component({
  selector: 'app-not-allowed',
  template: `
    <app-page-heading>You can only have one active request at any given time.</app-page-heading>
    <button (click)="onClick()" govukSecondaryButton type="button">View the active request</button>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class NotAllowedComponent implements OnInit {
  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly rdeStore: RdeStore,
    private readonly requestItemsService: RequestItemsService,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe(() => this.backLinkService.show());
  }

  onClick(): void {
    this.rdeStore
      .pipe(
        first(),
        map((state) => state?.requestId),
        switchMap((requestId) => this.requestItemsService.getItemsByRequest(requestId)),
        first(),
        map((res) => this.sortTimeline(res.items).find((action) => waitForRfiRdeTypes.includes(action.taskType))),
      )
      .subscribe((res) => {
        const isAviation = AVIATION_REQUEST_TYPES.includes(res.requestType) ? '/aviation' : '';
        return (res.taskType as string).includes('WAIT_FOR_RFI_RESPONSE')
          ? this.router.navigate([isAviation + '/rfi', res.taskId, 'wait'])
          : this.router.navigate([isAviation + '/rde', res.taskId, 'manual-approval']);
      });
  }

  private sortTimeline(res: ItemDTO[]): ItemDTO[] {
    return res
      .slice()
      .sort((a, b) => new Date(a.creationDate).getTime() - new Date(b.creationDate).getTime())
      .reverse();
  }
}
