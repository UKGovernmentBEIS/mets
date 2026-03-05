import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { BaseDataGapsComponent } from '../base-data-gaps.component';

@Component({
  selector: 'app-substitute-data',
  templateUrl: './substitute-data.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubstituteDataComponent extends BaseDataGapsComponent {
  constructor() {
    super();
  }

  form = this.fb.group({
    substituteData: this._form.controls.substituteData,
  });
  onSubmit() {
    this._form.updateValueAndValidity();
    if (this.form.valid) {
      this.store.empUkEtsDelegate
        .saveEmp({ dataGaps: this._form.getRawValue() }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../other-data-gaps-types'], {
            relativeTo: this.route,
            queryParams: { change: true },
          });
        });
    }
  }
}
