import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { BaseDataGapsComponent } from '../base-data-gaps.component';

@Component({
  selector: 'app-other-data-gaps-types',
  templateUrl: './other-data-gaps-types.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OtherDataGapsTypesComponent extends BaseDataGapsComponent {
  constructor() {
    super();
  }
  form = this.fb.group({
    otherDataGapsTypes: this._form.controls.otherDataGapsTypes,
  });
  onSubmit() {
    this._form.updateValueAndValidity();
    if (this.form.valid) {
      this.store.empUkEtsDelegate
        .saveEmp({ dataGaps: this._form.getRawValue() }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../summary'], {
            relativeTo: this.route,
          });
        });
    }
  }
}
