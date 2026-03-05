import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';

import { AccountNotesService } from 'pmrv-api';

@Component({
  selector: 'app-delete-account-note',
  templateUrl: './delete-account-note.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteAccountNoteComponent implements OnInit {
  accountId$ = this.route.paramMap.pipe(map((parameters) => +parameters.get('accountId')));
  public readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain);
  domain: string;

  constructor(
    readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly accountNotesService: AccountNotesService,
    readonly pendingRequest: PendingRequestService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.currentDomain$.pipe(takeUntil(this.destroy$)).subscribe((domain) => {
      this.domain = domain === 'AVIATION' ? domain.toLowerCase() + '/' : '';
    });
  }

  onDelete() {
    this.route.paramMap
      .pipe(
        first(),
        map((parameters) => +parameters.get('noteId')),
        switchMap((noteId) => this.accountNotesService.deleteAccountNote(noteId)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() =>
        this.router.navigate([`${this.domain}accounts/${this.route.snapshot.paramMap.get('accountId')}`], {
          fragment: 'notes',
        }),
      );
  }
}
