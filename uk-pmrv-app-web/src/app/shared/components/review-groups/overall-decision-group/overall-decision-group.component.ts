import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import {
  NotVerifiedOverallAssessment,
  NotVerifiedReason,
  VerifiedSatisfactoryOverallAssessment,
  VerifiedWithCommentsOverallAssessment,
} from 'pmrv-api';

@Component({
  selector: 'app-overall-decision-group',
  templateUrl: './overall-decision-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionGroupComponent implements OnInit {
  @Input() isEditable = false;
  @Input() overallAssessment:
    | VerifiedSatisfactoryOverallAssessment
    | VerifiedWithCommentsOverallAssessment
    | NotVerifiedOverallAssessment;

  reasons: Array<string>;
  notVerifiedReasons: Array<NotVerifiedReason>;

  ngOnInit(): void {
    this.reasons =
      this.overallAssessment?.type === 'VERIFIED_WITH_COMMENTS'
        ? (this.overallAssessment as VerifiedWithCommentsOverallAssessment).reasons
        : undefined;

    this.notVerifiedReasons =
      this.overallAssessment?.type === 'NOT_VERIFIED'
        ? (this.overallAssessment as NotVerifiedOverallAssessment).notVerifiedReasons
        : undefined;
  }
}
