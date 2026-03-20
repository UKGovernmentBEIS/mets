import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { VerificationDataItem } from '../../types/verification-data-item.type';

@Component({
  selector: 'app-verification-recommendation-item',
  standalone: false,
  templateUrl: './verification-recommendation-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationRecommendationItemComponent {
  @Input() verificationDataItem: VerificationDataItem;
}
