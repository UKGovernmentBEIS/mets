import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { RequiredChange } from './changes-requested-template.type';

@Component({
  selector: 'app-changes-requested-template',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './changes-requested-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangesRequestedTemplateComponent {
  @Input() requiredChanges: Array<RequiredChange> = [];
  @Input() reviewAttachments: { [key: string]: string };
  @Input() notes: string;
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
