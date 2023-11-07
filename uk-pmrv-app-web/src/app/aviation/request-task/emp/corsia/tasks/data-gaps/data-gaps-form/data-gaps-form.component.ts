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
  heading: string;
  readonly form = this.fb.group({
    dataGaps: this._form.controls.dataGaps,
  });
  constructor() {
    super();
    this.heading = this.isCert ? 'Handling of data gaps and incorrect data values' : 'Monitoring of data gaps';
  }

  onSubmit() {
    this._form.updateValueAndValidity();

    if (this.form.valid) {
      this.store.empCorsiaDelegate
        .saveEmp({ dataGaps: this._form.getRawValue() }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          const route = this.isCert ? './secondary-data-sources-exist' : './secondary-data-sources';
          this.router.navigate([route], {
            relativeTo: this.route,
          });
        });
    }
  }
}
