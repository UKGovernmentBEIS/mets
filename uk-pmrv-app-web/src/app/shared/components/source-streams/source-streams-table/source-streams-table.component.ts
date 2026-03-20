import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PipesModule } from '@shared/pipes/pipes.module';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { SourceStream } from 'pmrv-api';

@Component({
  selector: 'app-source-streams-table',
  imports: [CommonModule, GovukComponentsModule, PipesModule, RouterLink],
  templateUrl: './source-streams-table.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SourceStreamsTableComponent {
  data = input.required<SourceStream[]>();
  isEditable = input.required<boolean>();

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference' },
    { field: 'description', header: 'Description' },
    { field: 'type', header: 'Type' },
    { field: 'change', header: '' },
    { field: 'delete', header: '' },
  ];
}
