import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { OperatorDetailsActivitiesDescriptionPipe } from '@aviation/shared/pipes/operator-details-activities-description.pipe';
import { OperatorDetailsFlightIdentificationTypePipe } from '@aviation/shared/pipes/operator-details-flight-identification-type.pipe';
import { OperatorDetailsLegalStatusTypePipe } from '@aviation/shared/pipes/operator-details-legal-status-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { AviationCorsiaOperatorDetails, EmpCorsiaOperatorDetails, EmpOperatorDetails } from 'pmrv-api';

@Component({
  selector: 'app-operator-details-summary-template',
  templateUrl: './operator-details-summary-template.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    RouterLink,
    SharedModule,
    OperatorDetailsFlightIdentificationTypePipe,
    OperatorDetailsLegalStatusTypePipe,
    OperatorDetailsActivitiesDescriptionPipe,
  ],
})
export class OperatorDetailsSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() isCorsia = false;
  @Input() data: EmpOperatorDetails | EmpCorsiaOperatorDetails | AviationCorsiaOperatorDetails;
  @Input() certificationFiles: { fileName: string; downloadUrl: string }[];
  @Input() evidenceFiles: { fileName: string; downloadUrl: string }[];
  @Input() changeUrlQueryParams: Params = {};
}
