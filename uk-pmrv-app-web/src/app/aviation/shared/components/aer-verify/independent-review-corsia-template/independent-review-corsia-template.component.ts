import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaIndependentReview } from 'pmrv-api';

@Component({
  selector: 'app-independent-review-corsia-template',
  templateUrl: './independent-review-corsia-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndependentReviewCorsiaTemplateComponent {
  @Input() data: AviationAerCorsiaIndependentReview;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
