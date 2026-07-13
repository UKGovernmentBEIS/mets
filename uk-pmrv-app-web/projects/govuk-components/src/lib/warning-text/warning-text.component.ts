import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { GovukSpacingUnit } from '../types';

@Component({
  selector: 'govuk-warning-text',
  templateUrl: './warning-text.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WarningTextComponent {
  readonly assistiveText = input('Warning');
  readonly bottomSpacing = input<GovukSpacingUnit>(6);
}
