import { ChangeDetectionStrategy, Component } from '@angular/core';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-include-answer-details',
  imports: [GovukComponentsModule],
  templateUrl: './include-answer-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IncludeAnswerDetailsComponent {}
