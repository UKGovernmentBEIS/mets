import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges } from '@angular/core';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { AviationAerCorsiaAircraftTypeDetails } from 'pmrv-api';

import { defaultColumns } from './column-header-mapping';

@Component({
  selector: 'app-aircraft-fuel-burn-ratio-table',
  templateUrl: './aircraft-fuel-burn-ratio-table.component.html',
  standalone: true,
  imports: [GovukComponentsModule, CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AircraftFuelBurnRatioTableComponent implements OnChanges {
  columns = defaultColumns;

  @Input()
  headingText: string;

  @Input()
  aviationAerCorsiaAircraftTypeDetails: AviationAerCorsiaAircraftTypeDetails[];

  @Input() set customColumns(columns: GovukTableColumn[]) {
    this.columns = columns;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.customColumns && changes.customColumns.currentValue) {
      this.columns = changes.customColumns.currentValue;
    }
  }
}
