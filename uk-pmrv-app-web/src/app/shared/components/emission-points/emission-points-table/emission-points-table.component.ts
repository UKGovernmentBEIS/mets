import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PipesModule } from '@shared/pipes/pipes.module';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { EmissionPoint } from 'pmrv-api';

@Component({
  selector: 'app-emission-points-table',
  imports: [CommonModule, GovukComponentsModule, PipesModule, RouterLink],
  templateUrl: './emission-points-table.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionPointsTableComponent {
  data = input.required<EmissionPoint[]>();
  isEditable = input.required<boolean>();

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference' },
    { field: 'description', header: 'Description' },
    { field: 'change', header: '', widthClass: 'app-column-width-5-per' },
    { field: 'delete', header: '', widthClass: 'app-column-width-5-per' },
  ];
}
