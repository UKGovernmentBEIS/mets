import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { EmpDetermination } from '@aviation/request-task/emp/shared/util/emp.util';
import { EmpReviewDeterminationTypePipe } from '@aviation/shared/pipes/review-determination-type.pipe';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-overall-decision-summary-template',
  standalone: true,
  imports: [RouterLink, SharedModule, EmpReviewDeterminationTypePipe],
  templateUrl: './overall-decision-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionSummaryTemplateComponent {
  @Input() data: EmpDetermination;
  @Input() variationScheduleItems: string[] = [];
  @Input() isEditable: boolean;
  @Input() changeUrlQueryParams: Params = {};
}
