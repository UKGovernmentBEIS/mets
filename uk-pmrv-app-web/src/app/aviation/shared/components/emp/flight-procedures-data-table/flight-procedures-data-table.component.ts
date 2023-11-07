import { CommonModule, NgSwitch, NgSwitchCase, NgSwitchDefault } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { EmpOperatingStatePairsCorsiaDetails } from 'pmrv-api';

import { defaultColumns } from './column-header-mapping';

@Component({
  selector: 'app-flight-procedures-data-table',
  templateUrl: './flight-procedures-data-table.component.html',
  standalone: true,
  imports: [GovukComponentsModule, NgSwitch, NgSwitchCase, NgSwitchDefault, CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FlightProceduresDataTableComponent implements OnInit, OnChanges {
  @Input() headingText = '';
  @Input() operatingStatePairsCorsiaDetails: EmpOperatingStatePairsCorsiaDetails[];
  @Input() customColumns?: GovukTableColumn[];

  protected columns = defaultColumns;

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
