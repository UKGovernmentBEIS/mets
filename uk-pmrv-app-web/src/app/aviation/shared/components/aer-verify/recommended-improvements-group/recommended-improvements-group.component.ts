import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { VerifierComment } from 'pmrv-api';

@Component({
  selector: 'app-recommended-improvements-group-template',
  templateUrl: './recommended-improvements-group.component.html',
  imports: [GovukComponentsModule, SharedModule, RouterModule],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsGroupComponent {
  @Input() isEditable = false;
  @Input() verifierComments: VerifierComment[];
  @Input() queryParams = {};
  @Input() baseUrl = './';

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-input--width-20' },
    { field: 'explanation', header: 'Explanation', widthClass: 'govuk-input--width-20' },
    { field: 'change', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];
}
