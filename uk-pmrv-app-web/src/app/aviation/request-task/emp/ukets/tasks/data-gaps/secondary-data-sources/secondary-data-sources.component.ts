import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { BaseDataGapsComponent } from '../base-data-gaps.component';

@Component({
  selector: 'app-secondary-data-sources',
  templateUrl: './secondary-data-sources.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SecondaryDataSourcesComponent extends BaseDataGapsComponent {
  constructor() {
    super();
  }

  form = this.fb.group({
    secondaryDataSources: this._form.controls.secondaryDataSources,
  });
  onSubmit() {
    this._form.updateValueAndValidity();
    if (this.form.valid) {
      this.store.empUkEtsDelegate
        .saveEmp({ dataGaps: this._form.getRawValue() }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../substitute-data'], {
            relativeTo: this.route,
          });
        });
    }
  }
}
