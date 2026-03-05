import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, startWith, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { HoldingCompanyFormComponent } from '@shared/holding-company-form';
import { legalEntityTypeMap } from '@shared/interfaces/legal-entity';
import { originalOrder } from '@shared/keyvalue-order';

import { InstallationAccountUpdateService, LegalEntityDTO } from 'pmrv-api';

import { LEGAL_ENTITY_FORM_REG } from '../../installation-account-application/factories/legal-entity/legal-entity-form-reg.factory';
import { ApplicationSectionType } from '../../installation-account-application/store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../../installation-account-application/store/installation-account-application.store';

@Component({
  selector: 'app-legal-entity-details',
  templateUrl: './legal-entity-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class LegalEntityDetailsComponent implements OnInit, AfterViewInit {
  @ViewChild('noRefNoReason', { read: ElementRef }) noRefNoReason: ElementRef;

  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly originalOrder = originalOrder;
  form: UntypedFormGroup;
  radioOptions = legalEntityTypeMap;
  edit = false;
  heading: string;

  private accountId: number;

  constructor(
    public readonly store: InstallationAccountApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly fb: UntypedFormBuilder,
    @Inject(LEGAL_ENTITY_FORM_REG) private readonly legalEntityForm: UntypedFormGroup,
    private readonly accountUpdateService: InstallationAccountUpdateService,
    private router: Router,
  ) {
    this.form = this.legalEntityForm.get('detailsGroup') as UntypedFormGroup;
    this.form.markAsPristine();
    this.form.markAsUntouched();

    this.belongsToHoldingCompanyCtrl.valueChanges
      .pipe(startWith(this.belongsToHoldingCompanyCtrl.value), takeUntil(this.destroy$))
      .subscribe((b) => {
        if (b) {
          this.form.addControl('holdingCompanyGroup', this.fb.group(HoldingCompanyFormComponent.controlsFactory()));
        } else if (this.form.contains('holdingCompanyGroup')) {
          this.form.removeControl('holdingCompanyGroup');
        }
      });

    if (this.holdingCompanyForm && this.holdingCompanyForm.get('name').value) {
      this.belongsToHoldingCompanyCtrl.setValue(true);
    }
  }

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        map((params) => +params.get('accountId')),
        takeUntil(this.destroy$),
      )
      .subscribe((accountId) => (this.accountId = accountId));

    this.route.data.pipe(takeUntil(this.destroy$)).subscribe((data) => {
      if (data.context) {
        this.edit = data.context === 'edit';
      }
      this.heading = this.edit ? 'Edit the organisation details' : 'Add the organisation details';
      if (data && data.accountPermit) {
        this.setFormValue(data.accountPermit.account.legalEntity);
      }
    });

    if (this.edit) {
      this.form.get('name').clearAsyncValidators();
    }
  }

  ngAfterViewInit(): void {
    if (this.noReferenceNumberReasonCtrl?.value) {
      const details = (this.noRefNoReason.nativeElement as HTMLElement).querySelector('details') as HTMLDetailsElement;
      details.open = true;
    }
  }

  onSubmit(): void {
    // The parent form is not submitted, thus not updated
    this.legalEntityForm.updateValueAndValidity();

    if (this.edit) {
      const dto = this.assembleLegalEntityFromForm();
      this.accountUpdateService.updateInstallationAccountLegalEntity(this.accountId, dto).subscribe(() => {
        this.router.navigate(['../..'], { relativeTo: this.route });
      });
    } else {
      this.store.updateTask(ApplicationSectionType.legalEntity, this.legalEntityForm.value, 'complete');

      if (this.store.getState().isReviewed) {
        this.store.amend().subscribe(() => this.store.nextStep('../..', this.route));
      } else {
        this.store.nextStep('../..', this.route);
      }
    }
  }

  get belongsToHoldingCompanyCtrl(): AbstractControl {
    return this.form && this.form.get('belongsToHoldingCompany');
  }

  get holdingCompanyForm(): UntypedFormGroup | null {
    return (this.form && (this.form.get('holdingCompanyGroup') as UntypedFormGroup)) ?? null;
  }

  get noReferenceNumberReasonCtrl(): AbstractControl {
    return this.form && this.form.get('noReferenceNumberReason');
  }

  private assembleLegalEntityFromForm(): LegalEntityDTO {
    const value = this.form.value;
    if (value.holdingCompanyGroup) {
      value.holdingCompany = { ...value.holdingCompanyGroup };
      delete value.holdingCompanyGroup;
    }
    delete value.belongsToHoldingCompany;

    if (value.referenceNumber) {
      value.noReferenceNumberReason = null;
    }

    return value;
  }

  private setFormValue(legalEntity: LegalEntityDTO): void {
    this.belongsToHoldingCompanyCtrl.setValue(!!legalEntity.holdingCompany);
    const { id, ...value } = legalEntity as any;

    if (legalEntity.referenceNumber) {
      value.noReferenceNumberReason = null;
    } else {
      value.referenceNumber = null;
    }

    if (legalEntity.holdingCompany) {
      value.holdingCompanyGroup = { ...legalEntity.holdingCompany };
      value.holdingCompanyGroup.address.line2 = legalEntity.holdingCompany.address.line2 ?? null;
      value.holdingCompanyGroup.registrationNumber = legalEntity.holdingCompany.registrationNumber ?? null;
      delete value.holdingCompany;
    }

    value.belongsToHoldingCompany = !!legalEntity.holdingCompany;
    value.address.line2 = legalEntity.address.line2 ?? null;

    this.form.setValue(value);
  }
}
