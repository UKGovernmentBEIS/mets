import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Subscription, switchMap, take, tap } from 'rxjs';

import { AuthStore } from '@core/store';
import produce from 'immer';

import { AviationAccountFormModel, AviationAccountFormProvider } from '../../services';
import { AviationAccountsStore, selectAccount, selectAccountInfo } from '../../store';

@Component({
  selector: 'app-edit-aviation-account',
  templateUrl: './edit-aviation-account.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditAviationAccountComponent implements OnInit, AfterViewInit, OnDestroy {
  form: FormGroup<{ account: FormGroup<AviationAccountFormModel> }>;
  accountInfo$: Subscription;
  checkForUkAviation = false;
  accountInfoData: {
    competentAuthority: 'ENGLAND' | 'NORTHERN_IRELAND' | 'OPRED' | 'SCOTLAND' | 'WALES';
    accountType: 'AVIATION' | 'INSTALLATION';
  };

  constructor(
    private readonly formProvider: AviationAccountFormProvider,
    private readonly fb: UntypedFormBuilder,
    private readonly store: AviationAccountsStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly authStore: AuthStore,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({ account: this.formProvider.form });
    this.formProvider.addFieldsForEdit();
    this.accountInfo$ = this.store.pipe(selectAccountInfo).subscribe((response) => {
      this.accountInfoData = {
        competentAuthority: response?.competentAuthority,
        accountType: response?.accountType,
      };

      this.checkForUkAviation = response?.emissionTradingScheme === 'UK_ETS_AVIATION';
      this.form.patchValue({
        account: {
          name: response?.name,
          emissionTradingScheme: response?.emissionTradingScheme,
          crcoCode: response?.crcoCode,
          sopId: response?.sopId ? (response.sopId.toString() as any) : null,
          commencementDate: new Date(response?.commencementDate) as any,
          registryId: response?.registryId ? response.registryId.toString() : null,
          id: response?.id,
          hasContactAddress: [!!response?.location],
          location: response?.location,
        },
      });
    });
  }

  ngAfterViewInit(): void {
    this.cdr.detectChanges();
  }

  ngOnDestroy(): void {
    this.accountInfo$.unsubscribe();
    this.formProvider.resetForm();
  }

  onContinue() {
    if (this.form.valid) {
      this.store
        .pipe(selectAccount, take(1))
        .pipe(
          tap((account) => {
            this.store.setCurrentAccount(
              produce(account, (updated) => {
                updated.aviationAccount = {
                  ...this.formProvider.formValue,
                  competentAuthority: this.accountInfoData.competentAuthority,
                  accountType: this.accountInfoData.accountType,
                  reportingStatus: account.aviationAccount.reportingStatus,
                  reportingStatusReason: account.aviationAccount.reportingStatusReason,
                  location: this.formProvider.form.get('hasContactAddress').value.includes(true)
                    ? this.formProvider.form.get('location').value
                    : null,
                };
              }),
            );
          }),
          switchMap(() => this.store.editAccount()),
        )
        .subscribe(async () => {
          await this.router.navigate(['../'], { relativeTo: this.route });
        });
    }
  }
}
