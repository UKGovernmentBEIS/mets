import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ValidationErrors } from '@angular/forms';

import { AttachedFile } from '@shared/types/attached-file.type';

import { RegulatorAirImprovementResponse } from 'pmrv-api';

@Component({
  selector: 'app-air-regulator-response-item',
  templateUrl: './air-regulator-response-item.component.html',
  styleUrls: ['./air-regulator-response-item.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AirRegulatorResponseItemComponent {
  @Input() isEditable = false;
  @Input() isReview = false;
  @Input() reference: string;
  @Input() regulatorAirImprovementResponse: RegulatorAirImprovementResponse;
  @Input() attachedFiles: AttachedFile[];
  @Input() errors: ValidationErrors;
}
