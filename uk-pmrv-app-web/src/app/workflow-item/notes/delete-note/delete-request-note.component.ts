import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, takeUntil, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store/auth';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { RequestNotesService } from 'pmrv-api';

import { WorkflowItemAbstractComponent } from '../../workflow-item-abstract.component';

@Component({
  selector: 'app-delete-request-note',
  templateUrl: './delete-request-note.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class DeleteRequestNoteComponent extends WorkflowItemAbstractComponent implements OnInit {
  constructor(
    protected readonly authStore: AuthStore,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    protected readonly backLinkService: BackLinkService,
    protected readonly destroy$: DestroySubject,
    private readonly requestNotesService: RequestNotesService,
    private readonly pendingRequest: PendingRequestService,
  ) {
    super(authStore, router, route, backLinkService, destroy$);
  }

  ngOnInit(): void {
    this.prefixUrl$
      .pipe(withLatestFrom(this.accountId$, this.requestId$), takeUntil(this.destroy$))
      .subscribe(([prefixUrl, accountId, requestId]) =>
        accountId
          ? this.backLinkService.show(`${prefixUrl}/workflows/${requestId}`, 'notes')
          : this.backLinkService.show(`${prefixUrl}/${requestId}`, 'notes'),
      );
  }

  onDelete() {
    this.route.paramMap
      .pipe(
        first(),
        map((parameters) => +parameters.get('noteId')),
        switchMap((noteId) => this.requestNotesService.deleteRequestNote(noteId)),
        withLatestFrom(this.accountId$, this.prefixUrl$, this.requestId$),
        this.pendingRequest.trackRequest(),
      )
      //eslint-disable-next-line @typescript-eslint/no-unused-vars
      .subscribe(([response, accountId, prefixUrl, requestId]) =>
        this.router.navigate([accountId ? `${prefixUrl}/workflows/${requestId}` : `${prefixUrl}/${requestId}`], {
          fragment: 'notes',
        }),
      );
  }
}
