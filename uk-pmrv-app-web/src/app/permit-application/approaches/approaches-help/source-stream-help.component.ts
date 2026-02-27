import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-source-stream-help',
  standalone: false,
  template: `
    <app-permit-task>
      <app-source-stream-help-template></app-source-stream-help-template>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SourceStreamHelpComponent {}
