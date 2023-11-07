import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnChanges, OnInit } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { NotVerifiedDecisionReasonTypePipe } from '@aviation/shared/pipes/not-verified-reason.pipe';
import { AviationOverallDecisionTypePipe } from '@aviation/shared/pipes/overall-decision-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import {
  AviationAerNotVerifiedDecision,
  AviationAerNotVerifiedDecisionReason,
  AviationAerVerifiedSatisfactoryDecision,
  AviationAerVerifiedSatisfactoryWithCommentsDecision,
} from 'pmrv-api';

@Component({
  selector: 'app-aviation-overall-decision-group',
  templateUrl: './overall-decision-group.component.html',

  imports: [
    GovukComponentsModule,
    CommonModule,
    SharedModule,
    ReturnToLinkComponent,
    RouterLinkWithHref,
    AviationOverallDecisionTypePipe,
    NotVerifiedDecisionReasonTypePipe,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionGroupComponent implements OnInit, OnChanges {
  @Input() isEditable = false;
  @Input() isCorsia = false;
  @Input() overallAssessment:
    | AviationAerVerifiedSatisfactoryDecision
    | AviationAerVerifiedSatisfactoryWithCommentsDecision
    | AviationAerNotVerifiedDecision;

  reasons: Array<string>;
  notVerifiedReasons: Array<AviationAerNotVerifiedDecisionReason>;

  ngOnInit(): void {
    this.initializeReasons();
  }

  ngOnChanges() {
    this.initializeReasons();
  }

  initializeReasons() {
    this.reasons =
      this.overallAssessment?.type === 'VERIFIED_AS_SATISFACTORY_WITH_COMMENTS'
        ? (this.overallAssessment as AviationAerVerifiedSatisfactoryWithCommentsDecision)?.reasons
        : undefined;

    this.notVerifiedReasons =
      this.overallAssessment?.type === 'NOT_VERIFIED'
        ? (this.overallAssessment as AviationAerNotVerifiedDecision)?.notVerifiedReasons
        : undefined;
  }
}
