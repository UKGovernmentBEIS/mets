import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { ALRAlrDataRegulatorReviewRequiredChange } from 'pmrv-api';

@Component({
  selector: 'app-changes-requested-template',
  standalone: true,
  imports: [SharedModule],
  template: `
    <dl govuk-summary-list class="govuk-summary-list--no-border summary-list--edge-border">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Changes required</dt>
        <dd govukSummaryListRowValue>
          <ng-container *ngFor="let requiredChange of requiredChanges; let i = index; let isLast = last">
            <div>
              {{ i + 1 }}. {{ requiredChange?.reason }}
              <br />
              <ng-container *ngIf="requiredChange?.files?.length > 0">
                <app-summary-download-files
                  [files]="getDownloadUrlFiles(this.requiredChange?.files)"></app-summary-download-files>
              </ng-container>
            </div>
            <br *ngIf="!isLast" />
          </ng-container>
        </dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangesRequestedTemplateComponent {
  @Input() requiredChanges: Array<ALRAlrDataRegulatorReviewRequiredChange> = [];
  @Input() reviewAttachments: { [key: string]: string };
  @Input() downloadUrl: string;

  getDownloadUrlFiles(files: string[]): AttachedFile[] {
    return (
      files?.map((id) => ({
        downloadUrl: this.downloadUrl + `${id}`,
        fileName: this.reviewAttachments[id],
      })) ?? []
    );
  }
}
