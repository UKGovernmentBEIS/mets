import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukSelectOption } from 'govuk-components';

const UKETSHeading =
  'Do you have an Air Operating Certificate (AOC) or equivalent, such as an Air Carrier Operating Certificate or Air Operator Certification?';
const CorsiaHeading =
  'Do you have an Air Operator Certificate (AOC) or equivalent, such as an Air Carrier Operating Certificate?';

@Component({
  selector: 'app-operator-details-air-operating-certificate-template',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReactiveFormsModule],
  templateUrl: './operator-details-air-operating-certificate-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsAirOperatingCertificateTemplateComponent implements OnInit {
  @Input() form: FormGroup<any>;
  @Input() issuingAuthorityOptions: GovukSelectOption<string>[];
  @Input() downloadUrl: string;
  @Input() isCorsia = false;

  @Output()
  readonly submitForm = new EventEmitter<FormGroup<any>>();

  heading = UKETSHeading;

  ngOnInit(): void {
    if (this.isCorsia) this.heading = CorsiaHeading;
  }
}
