import { ChangeDetectionStrategy, Component } from '@angular/core';

import { VERSION } from '../../environments/version';

@Component({
  selector: 'app-version',
  template: `
    <app-page-heading caption="Information about the application version" size="l">About</app-page-heading>

    <p class="govuk-body">
      Version:
      <span class="govuk-!-font-weight-bold">RELEASE_VERSION</span>
    </p>
    <p class="govuk-body">
      Commit hash:
      <span class="govuk-!-font-weight-bold">{{ version.hash }}</span>
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VersionComponent {
  version = VERSION;
}
