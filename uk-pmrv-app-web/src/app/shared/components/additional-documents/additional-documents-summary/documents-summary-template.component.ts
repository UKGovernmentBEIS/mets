import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { AdditionalDocuments } from 'pmrv-api';

@Component({
  selector: 'app-documents-summary-template',
  templateUrl: './documents-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsSummaryTemplateComponent implements OnInit {
  @Input() isEditable = false;
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;
  @Input() data: AdditionalDocuments;
  @Input() files: { downloadUrl: string; fileName: string }[];
  @Input() changeQueryParams = {};

  hasAttachments: boolean;

  ngOnInit(): void {
    this.hasAttachments = (this.files || []).length > 0;
  }
}
