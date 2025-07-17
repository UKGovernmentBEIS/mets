import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AviationAerCorsia3YearPeriodOffsettingTableData } from '@aviation/shared/types';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import { AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData } from 'pmrv-api';
@Component({
  selector: 'app-3year-offsetting-requirements-table-template',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './offsetting-requirements-table-template.component.html',
  styles: `
    .bold-text {
      font-weight: bold;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  viewProviders: [existingControlContainer],
})
export class ThreeYearOffsettingRequirementsTableTemplateComponent {
  @Input() data: AviationAerCorsia3YearPeriodOffsettingTableData[];
  @Input() totalYearlyOffsettingData: AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData;
  @Input() periodOffsettingRequirements: AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData;
  @Input() useTableWithInputs: boolean;

  columns: GovukTableColumn<AviationAerCorsia3YearPeriodOffsettingTableData>[] = [
    { field: 'schemeYear', header: 'Scheme year', isHeader: true },
    { field: 'calculatedAnnualOffsetting', header: 'Calculated Annual Offsetting (tCO2)', alignRight: true },
    { field: 'cefEmissionsReductions', header: 'CEF Emissions Reductions (tCO2)', alignRight: true },
  ];
}
