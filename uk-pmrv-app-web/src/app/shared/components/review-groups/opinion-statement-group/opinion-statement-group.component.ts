import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import {
  Aer,
  AerApplicationCompletedRequestActionPayload,
  AerApplicationReviewRequestTaskPayload,
  AerApplicationVerificationSubmitRequestTaskPayload,
  OpinionStatement,
} from 'pmrv-api';

@Component({
  selector: 'app-opinion-statement-group',
  templateUrl: './opinion-statement-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OpinionStatementGroupComponent implements OnInit {
  @Input() isEditable = false;
  @Input() showVerifiedData = false;
  @Input() payload:
    | AerApplicationVerificationSubmitRequestTaskPayload
    | AerApplicationReviewRequestTaskPayload
    | AerApplicationCompletedRequestActionPayload;
  opinionStatement: OpinionStatement;
  aerVerified: Aer;

  ngOnInit(): void {
    this.opinionStatement = this.payload.verificationReport.opinionStatement;
    this.aerVerified = this.showVerifiedData
      ? (this.payload as AerApplicationReviewRequestTaskPayload)?.verifiedAer
        ? (this.payload as AerApplicationReviewRequestTaskPayload).verifiedAer
        : this.payload.aer
      : this.payload.aer;
  }
}
