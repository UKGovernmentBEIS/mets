import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { AerAmendGroup, amendTasksPerReviewGroup } from '../../../core/aer.amend.types';
import { AerService } from '../../../core/aer.service';

@Component({
  selector: 'app-amend-summary-template',
  templateUrl: './amend-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AmendSummaryTemplateComponent {
  section = this.route.snapshot.paramMap.get('section') as AerAmendGroup;

  amendTasksPerReviewGroup = amendTasksPerReviewGroup;
  constructor(private readonly route: ActivatedRoute, readonly aerService: AerService) {}
}
