import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { take } from 'rxjs';

import { AviationAccountFormModel, AviationAccountFormProvider } from '../../services';
import { AviationAccountsStore, selectNewAccount } from '../../store';

@Component({
  selector: 'app-create-aviation-account',
  templateUrl: './create-aviation-account.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateAviationAccountComponent implements OnInit {
  form: FormGroup<{ account: FormGroup<AviationAccountFormModel> }>;

  constructor(
    private readonly formProvider: AviationAccountFormProvider,
    private readonly fb: UntypedFormBuilder,
    private readonly store: AviationAccountsStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({ account: this.formProvider.form });
    this.store.pipe(selectNewAccount).pipe(take(1)).subscribe(this.formProvider.resetForm);
  }

  onContinue(): void {
    if (this.form.valid) {
      this.store.setIsInitiallySubmitted(true);
      this.store.setNewAccount(this.formProvider.formValue);
      this.router.navigate(['summary'], { relativeTo: this.route });
    }
  }
}
