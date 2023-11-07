import { CommonModule, Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AircraftTypeDescriptionPipe } from '@aviation/shared/pipes/aircraft-type-description.pipe';
import { AircraftTypeFuelTypesPipe } from '@aviation/shared/pipes/aircraft-type-fuel-types.pipe';
import { AircraftTypeFuelMethodPipe } from '@aviation/shared/pipes/aircraft-type-method.pipe';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import { AircraftTypeDetails, AircraftTypeDetailsCorsia } from 'pmrv-api';

export type AircraftTypeDetailsWithIndex = (AircraftTypeDetails | AircraftTypeDetailsCorsia) & { idx: number };

/** Convenience function to append the aircraft type index to the aircraft type details object */
export const appendIndex = <T extends AircraftTypeDetails | AircraftTypeDetailsCorsia>(at: T, idx: number) => ({
  ...at,
  idx,
});

const pipes = [AircraftTypeFuelTypesPipe, AircraftTypeDescriptionPipe, AircraftTypeFuelMethodPipe];

@Component({
  selector: 'app-aircraft-type-table',
  templateUrl: './aircraft-type-table.component.html',
  standalone: true,
  imports: [CommonModule, RouterModule, SharedModule, ...pipes],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./aircraft-type-table.component.scss'],
})
export class AircraftTypeTableComponent implements OnInit {
  @Input()
  aircraftTypes: AircraftTypeDetailsWithIndex[] | undefined;
  @Input()
  editable = true;
  @Input() isFUMM = false;
  @Input() isCorsia = false;
  columns: GovukTableColumn[] = [];
  location = inject(Location);
  changeUrl = ['aircraft-type', 'edit'];
  removeUrl = ['aircraft-type', 'remove'];
  ngOnInit(): void {
    this.columns = [
      {
        header: 'Aircraft Type',
        field: 'aircraftTypeInfo',
      },
      {
        header: 'Sub-type',
        field: 'subtype',
      },
      {
        header: 'Number',
        field: 'numberOfAircrafts',
      },
      {
        header: 'Fuels used',
        field: 'fuelTypes',
      },
      {
        header: 'Method',
        field: 'fuelConsumptionMeasuringMethod',
      },
      {
        header: '',
        field: 'actions',
      },
    ];
    if (this.location.path().includes('summary')) {
      this.changeUrl.unshift('../');
      this.removeUrl.unshift('../');
    }
    if (!this.editable) {
      this.columns = this.columns.filter((c) => c.field !== 'actions');
    }
    if (!this.isFUMM) {
      this.columns = this.columns.filter((c) => c.field !== 'fuelConsumptionMeasuringMethod');
    }
  }
}
