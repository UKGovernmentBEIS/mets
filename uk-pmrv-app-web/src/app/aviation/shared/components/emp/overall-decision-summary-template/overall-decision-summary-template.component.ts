import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { EmpReviewDeterminationTypePipe } from '@aviation/shared/pipes/review-determination-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { EmpIssuanceDetermination } from 'pmrv-api';

@Component({
  selector: 'app-overall-decision-summary-template',
  standalone: true,
  imports: [RouterLink, SharedModule, EmpReviewDeterminationTypePipe],
  templateUrl: './overall-decision-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionSummaryTemplateComponent {
  @Input() data: EmpIssuanceDetermination;
  @Input() variationScheduleItems: string[] = [];
  @Input() isEditable: boolean;
  @Input() changeUrlQueryParams: Params = {};
}
