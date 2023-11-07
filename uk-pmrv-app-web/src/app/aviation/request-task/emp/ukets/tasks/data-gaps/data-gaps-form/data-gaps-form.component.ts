import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { BaseDataGapsComponent } from '../base-data-gaps.component';

@Component({
  selector: 'app-data-gaps',
  templateUrl: './data-gaps-form.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsFormComponent extends BaseDataGapsComponent {
  constructor() {
    super();
  }
  readonly form = this.fb.group({
    dataGaps: this._form.controls.dataGaps,
  });

  onSubmit() {
    this._form.updateValueAndValidity();
    if (this.form.valid) {
      this.store.empUkEtsDelegate
        .saveEmp({ dataGaps: this._form.getRawValue() }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['./secondary-data-sources'], {
            relativeTo: this.route,
            queryParams: { change: true },
          });
        });
    }
  }
}
