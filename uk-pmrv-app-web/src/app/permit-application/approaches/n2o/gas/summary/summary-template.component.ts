import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-n2o-approach-gas-summary-template',
  templateUrl: './summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() showOriginal = false;
  @Input() hasBottomBorder = true;
}
