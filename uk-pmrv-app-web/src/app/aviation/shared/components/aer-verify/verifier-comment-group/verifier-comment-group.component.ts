import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { VerifierComment } from 'pmrv-api';

@Component({
  selector: 'app-verifier-comment-group',
  templateUrl: './verifier-comment-group.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, RouterLinkWithHref],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierCommentGroupComponent {
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
