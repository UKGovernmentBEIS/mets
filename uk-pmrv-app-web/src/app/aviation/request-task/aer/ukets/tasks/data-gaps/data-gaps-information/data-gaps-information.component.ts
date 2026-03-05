import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { DataGapsInformationFormComponent } from '../../../../../../shared/components/aer/data-gaps/data-gaps-information-form';
import { DataGapsFormProvider } from '../data-gaps-form.provider';

@Component({
  selector: 'app-data-gaps-information',
  templateUrl: './data-gaps-information.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, DataGapsInformationFormComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DataGapsInformationComponent implements OnInit {
  protected form = this.formProvider.createDataGapGroup();

  get isAddPath() {
    return this.route.snapshot.url[0].path === 'add-data-gap-information';
  }

  get index() {
    return +this.route.snapshot.url[0].path;
  }

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: DataGapsFormProvider,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    if (!this.isAddPath) {
      const dataGaps = this.formProvider.getFormValue().dataGaps;

      if (dataGaps && !!dataGaps[this.index]) {
        this.form.setValue(dataGaps[this.index]);
      }
    }
  }

  onSubmit() {
    const value = this.formProvider.getFormValue()?.dataGaps ? [...this.formProvider.getFormValue().dataGaps] : [];

    if (value.length > 0 && !this.isAddPath) {
      value[this.index] = this.form.value;
    } else {
      value.push(this.form.value);
    }

    this.formProvider.dataGapsCtrl.setValue(value);
    this.router.navigate([this.isAddPath ? '..' : '../..'], { relativeTo: this.route, queryParams: { change: true } });
  }
}
