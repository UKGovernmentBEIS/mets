import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, take } from 'rxjs';

import { EmpBatchReissueStore } from '@aviation/workflows/emp-batch-reissue/submit/store/emp-batch-reissue.store';
import { SignatoryAbstractComponent } from '@shared/components/batch-reissue-submit/signatory-abstract.component';

import { RegulatorUserAuthorityInfoDTO } from 'pmrv-api';

@Component({
  selector: 'app-signatory',
  templateUrl: './signatory.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SignatoryComponent extends SignatoryAbstractComponent implements OnInit {
  constructor(
    readonly formBuilder: UntypedFormBuilder,
    readonly router: Router,
    readonly route: ActivatedRoute,
    private readonly store: EmpBatchReissueStore,
  ) {
    super(formBuilder, router, route);
  }

  ngOnInit(): void {
    super.init();
  }

  protected patchStore(value: any): void {
    this.store.patchState(value);
  }

  protected populateSignatoryFormControl(regulators: RegulatorUserAuthorityInfoDTO[]): void {
    this.store
      .pipe(
        take(1),
        map((storeState) => storeState.signatory),
      )
      .subscribe((signatory) => {
        const selectedRegulator = regulators.find((reg) => reg.userId === signatory);
        if (selectedRegulator) {
          this.form.get('signatory').setValue(selectedRegulator.userId);
        }
      });
  }
}
