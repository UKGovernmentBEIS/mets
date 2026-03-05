import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params } from '@angular/router';

import { Abbreviations } from 'pmrv-api';

@Component({
  selector: 'app-abbreviations-summary-template',
  templateUrl: './abbreviations-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AbbreviationsSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() cssClass: string;
  @Input() hasBottomBorder = true;
  @Input() data: Abbreviations;
  @Input() changeUrlQueryParams: Params = {};
}
