import { ChangeDetectionStrategy, Component, Input, PipeTransform } from '@angular/core';

import { reviewGroupHeading } from '../../review/utils/review.permit';

@Component({
  selector: 'app-review-sections',
  standalone: false,
  templateUrl: './review-sections.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewSectionsComponent {
  @Input()
  statusResolverPipe: PipeTransform;

  linkText = reviewGroupHeading;
}
