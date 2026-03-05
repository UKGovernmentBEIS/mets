import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-information-sources-summary-template',
  templateUrl: './information-sources-summary-template.component.html',
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InformationSourcesSummaryTemplateComponent {
  @Input() data: string[];
  @Input() editable: boolean;
  @Input() baseChangeLink: string;
}
