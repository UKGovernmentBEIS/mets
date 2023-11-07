import { CommonModule, NgSwitch, NgSwitchCase, NgSwitchDefault } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

import { defaultColumns } from '@aviation/shared/components/aer/flight-data-table/column-header-mapping';
import { getSummaryDescription } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import {
  AviationAerCorsiaAggregatedEmissionDataDetails,
  AviationAerUkEtsAggregatedEmissionDataDetails,
} from 'pmrv-api';

@Component({
  selector: 'app-flight-data-table',
  templateUrl: './flight-data-table.component.html',
  styleUrls: ['./flight-data-table.component.scss'],
  standalone: true,
  imports: [GovukComponentsModule, NgSwitch, NgSwitchCase, NgSwitchDefault, CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FlightDataTableComponent implements OnInit, OnChanges {
  @Input()
  headingText: string;

  @Input()
  emissionDataDetails:
    | AviationAerUkEtsAggregatedEmissionDataDetails[]
    | AviationAerCorsiaAggregatedEmissionDataDetails[];

  @Input()
  customColumns?: GovukTableColumn[];

  @Input()
  isCorsia: boolean;

  columns = defaultColumns;

  getSummaryDescription = getSummaryDescription;

  ngOnInit(): void {
    if (this.customColumns) {
      this.columns = this.customColumns;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.customColumns && changes.customColumns.currentValue) {
      this.columns = changes.customColumns.currentValue;
    }
  }
}
