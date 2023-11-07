import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map } from 'rxjs';

import { DreService } from '../../core/dre.service';

@Component({
  selector: 'app-information-sources',
  templateUrl: './information-sources.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InformationSourcesComponent {
  private readonly nextWizardStep = 'charge-operator';
  errorsExist$ = new BehaviorSubject<boolean>(false);

  form: UntypedFormGroup = this.fb.group({});

  informationSources$ = this.dreService.dre$.pipe(map((dre) => dre.informationSources || []));

  constructor(
    readonly dreService: DreService,
    private readonly fb: UntypedFormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route });
  }
}
