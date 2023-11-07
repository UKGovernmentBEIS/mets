import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { SharedStore } from '@shared/store/shared.store';
import { StoreContextResolver } from '@shared/store-resolver/store-context.resolver';

import { resolveRequestType } from '../../store-resolver/request-type.resolver';
import { PEER_REVIEW_DECISION_FORM, peerReviewDecisionFormProvider } from './peer-review-decision-form.provider';
import { resolveReturnToText } from './peer-review-decision-type-resolver';

@Component({
  selector: 'app-peer-review-decision',
  templateUrl: './peer-review-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [peerReviewDecisionFormProvider],
})
export class PeerReviewDecisionComponent implements OnInit {
  requestType = resolveRequestType(this.location.path());
  returnTo$ = this.storeResolver
    .getRequestTaskType(this.requestType)
    .pipe(map((requestTaskType) => resolveReturnToText(this.requestType, requestTaskType)));

  constructor(
    @Inject(PEER_REVIEW_DECISION_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly location: Location,
    private readonly route: ActivatedRoute,
    private readonly sharedStore: SharedStore,
    private storeResolver: StoreContextResolver,
  ) {}

  ngOnInit(): void {
    this.sharedStore
      .pipe(
        map((state) => state.peerReviewDecision),
        first(),
      )
      .subscribe((peerReviewDecision) => {
        this.form.controls['type'].setValue(peerReviewDecision?.type);
        this.form.controls['notes'].setValue(peerReviewDecision?.notes);
        this.form.updateValueAndValidity();
      });
  }

  onSubmit(): void {
    const type = this.form.get('type').value;
    const notes = this.form.get('notes').value;
    this.sharedStore.setState({
      ...this.sharedStore.getState(),
      peerReviewDecision: {
        type,
        notes,
      },
    });
    this.router.navigate(['answers'], {
      relativeTo: this.route,
      queryParams: this.route.snapshot.queryParams,
    });
  }
}
