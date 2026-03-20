import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { BaseDataGapsComponent } from '../base-data-gaps.component';

@Component({
  selector: 'app-secondary-data-sources-exist',
  imports: [SharedModule],
  templateUrl: './secondary-data-sources-exist.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SecondaryDataSourcesExistComponent extends BaseDataGapsComponent {
  form = this.fb.group(
    {
      secondarySourcesDataGapsExist: this._form.controls.secondarySourcesDataGapsExist,
      secondarySourcesDataGapsConditions: this._form.controls.secondarySourcesDataGapsConditions,
    },
    {
      updateOn: 'change',
    },
  );

  onSubmit() {
    this._form.updateValueAndValidity();

    if (this.form.valid) {
      const formValue = this._form.getRawValue();
      if (!formValue.secondarySourcesDataGapsExist) delete formValue.secondarySourcesDataGapsConditions;
      this.store.empCorsiaDelegate
        .saveEmp({ dataGaps: formValue }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../summary'], {
            relativeTo: this.route,
          });
        });
    }
  }
}
