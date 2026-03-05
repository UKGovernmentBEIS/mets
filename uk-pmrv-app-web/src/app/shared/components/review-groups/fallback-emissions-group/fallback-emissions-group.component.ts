import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { FallbackEmissions } from 'pmrv-api';

@Component({
  selector: 'app-fallback-emissions-group',
  templateUrl: './fallback-emissions-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FallbackEmissionsGroupComponent {
  @Input() fallbackEmissions: FallbackEmissions;
  @Input() sourceStreams: string[];
  @Input() documentFiles: { downloadUrl: string; fileName: string }[];
  @Input() isEditable = false;
}
