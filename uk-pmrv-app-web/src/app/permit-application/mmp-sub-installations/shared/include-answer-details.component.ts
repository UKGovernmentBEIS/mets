import { ChangeDetectionStrategy, Component } from '@angular/core';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-include-answer-details',
  templateUrl: './include-answer-details.component.html',
  standalone: true,
  imports: [GovukComponentsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IncludeAnswerDetailsComponent {}
