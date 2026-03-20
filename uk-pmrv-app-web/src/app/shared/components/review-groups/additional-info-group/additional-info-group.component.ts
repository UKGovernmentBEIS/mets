import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { Aer } from 'pmrv-api';

@Component({
  selector: 'app-additional-info-group',
  standalone: false,
  templateUrl: './additional-info-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalInfoGroupComponent {
  @Input() aerData: Aer;
  @Input() additionalDocumentFiles: { downloadUrl: string; fileName: string }[];
}
