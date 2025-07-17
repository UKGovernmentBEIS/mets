import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PipesModule } from '@shared/pipes/pipes.module';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { EmissionSource } from 'pmrv-api';

@Component({
  selector: 'app-emission-source-table',
  templateUrl: './emission-source-table.component.html',
  standalone: true,
  imports: [CommonModule, GovukComponentsModule, PipesModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSourceTableComponent {
  data = input.required<EmissionSource[]>();
  isEditable = input.required<boolean>();

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference' },
    { field: 'description', header: 'Description' },
    { field: 'change', header: '', widthClass: 'app-column-width-5-per' },
    { field: 'delete', header: '', widthClass: 'app-column-width-5-per' },
  ];
}
