import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { PermitSurrenderReviewDetermination } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { reasonFormProvider } from './reason-form.provider';

@Component({
  selector: 'app-reason',
  templateUrl: './reason.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reasonFormProvider],
})
export class ReasonComponent implements PendingRequest, OnInit {
  determinationType$ = this.store.pipe(map((state) => state?.reviewDetermination?.type));

  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onContinue(): void {
    if (!this.form.dirty) {
      this.determinationType$
        .pipe(first())
        .subscribe((type) => this.router.navigate([`../${this.nextStep(type)}`], { relativeTo: this.route }));
    } else {
      const reason = this.form.value.reason;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postReviewDetermination(
              {
                ...state.reviewDetermination,
                reason,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
          switchMap(() => this.determinationType$),
          first(),
        )
        .subscribe((type) => this.router.navigate([`../${this.nextStep(type)}`], { relativeTo: this.route }));
    }
  }

  private nextStep(type: PermitSurrenderReviewDetermination['type']) {
    switch (type) {
      case 'GRANTED':
        return 'stop-date';
      case 'REJECTED':
        return 'refusal';
      case 'DEEMED_WITHDRAWN':
        return 'answers';
      default:
        return '';
    }
  }
}
