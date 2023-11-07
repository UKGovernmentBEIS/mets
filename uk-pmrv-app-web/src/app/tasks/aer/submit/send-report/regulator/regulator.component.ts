import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { CommonTasksStore } from '../../../../store/common-tasks.store';

@Component({
  selector: 'app-regulator',
  templateUrl: './regulator.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorComponent implements OnInit {
  isSubmitted$ = new BehaviorSubject(false);

  constructor(
    readonly aerService: AerService,
    private readonly store: CommonTasksStore,
    private readonly pendingRequest: PendingRequestService,
    private readonly backlinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backlinkService.show();
  }

  onSubmit() {
    this.store.requestTaskType$
      .pipe(
        first(),
        map((requestTaskType) => {
          let actionType;

          switch (requestTaskType) {
            case 'AER_APPLICATION_SUBMIT':
              actionType = 'AER_SUBMIT_APPLICATION';
              break;
            case 'AER_APPLICATION_AMENDS_SUBMIT':
              actionType = 'AER_SUBMIT_APPLICATION_AMEND';
              break;
          }

          return actionType;
        }),
        switchMap((actionType) => this.aerService.postSubmit(actionType)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.isSubmitted$.next(true);
        this.backlinkService.hide();
      });
  }
}
