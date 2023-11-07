import { CommonModule, NgSwitch, NgSwitchCase, NgSwitchDefault } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { AviationAerAircraftDataDetails } from 'pmrv-api';

import { defaultColumns } from './column-header-mapping';

@Component({
  selector: 'app-aircraft-types-data-table',
  templateUrl: './aircraft-types-data-table.component.html',
  styles: [
    `
      .cell-container {
        max-width: 230px;
        min-width: 140px;
      }
      .amount {
        text-align: left;
      }
    `,
  ],
  standalone: true,
  imports: [GovukComponentsModule, NgSwitch, NgSwitchCase, NgSwitchDefault, CommonModule, SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AircraftTypesDataTableComponent implements OnInit, OnChanges {
  @Input()
  headingText: string;

  @Input()
  aviationAerAircraftDataDetails: AviationAerAircraftDataDetails[];

  @Input()
  customColumns?: GovukTableColumn[];
  columns = defaultColumns;

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
