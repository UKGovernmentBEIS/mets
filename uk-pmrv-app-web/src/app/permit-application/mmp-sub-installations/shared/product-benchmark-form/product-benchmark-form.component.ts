import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { FormArray, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { SharedModule } from '@shared/shared.module';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { GovukComponentsModule } from 'govuk-components';

import { FuelInputDataSourcePB, SubInstallation, VinylChlorideMonomerDataSource } from 'pmrv-api';

import { IncludeAnswerDetailsComponent } from '../include-answer-details.component';

@Component({
  selector: 'app-product-benchmark-form',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    ReactiveFormsModule,
    SharedPermitModule,
    IncludeAnswerDetailsComponent,
  ],
  templateUrl: './product-benchmark-form.component.html',
  styleUrl: './product-benchmark-form.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ProductBenchmarkFormComponent {
  @Input() form: FormGroup<any>;
  @Input() heading: string;
  @Input() caption: string;
  @Input() subInstallationType: SubInstallation['subInstallationType'];
  @Input() dataSourcesHeading: string = 'Data sources';
  @Input() dataSourcesText: string;
  @Input() dataSourcesField: string | Record<string, any> = 'Composition Data';
  @Input() dataSourcesType: '44' | '45' | '46' | Record<string, any> = '46';
  @Input() dataSourcesNames: string[] = null;
  @Input() downloadUrl: string;
  @Output() readonly submitForm = new EventEmitter<FormGroup<any>>();
  @Output() readonly addDataSource = new EventEmitter();
  @Output() readonly removeDataSource = new EventEmitter<number>();

  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;

  productBenchmark44Types: FuelInputDataSourcePB['fuelInput'][] = [
    'METHOD_MONITORING_PLAN',
    'LEGAL_METROLOGICAL_CONTROL',
    'OPERATOR_CONTROL_NOT_POINT_B',
    'NOT_OPERATOR_CONTROL_NOT_POINT_B',
    'INDIRECT_DETERMINATION',
    'OTHER_METHODS',
  ];

  productBenchmark45Types: VinylChlorideMonomerDataSource['detail'][] = [
    'LEGAL_METROLOGICAL_CONTROL_READING',
    'OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'INDIRECT_DETERMINATION_READING',
    'PROXY_CALCULATION_METHOD',
    'OTHER_METHODS',
  ];

  productBenchmark46Types: FuelInputDataSourcePB['weightedEmissionFactor'][] = [
    'CALCULATION_METHOD_MONITORING_PLAN',
    'LABORATORY_ANALYSES_SECTION_61',
    'SIMPLIFIED_LABORATORY_ANALYSES_SECTION_62',
    'CONSTANT_VALUES_STANDARD_SUPPLIER',
    'CONSTANT_VALUES_SCIENTIFIC_EVIDENCE',
  ];

  OnRemoveDataSource(index: number): void {
    this.removeDataSource.emit(index);
  }

  OnAddDataSource(): void {
    this.addDataSource.emit();
  }

  get dataSourcesFormArray(): FormArray {
    return this.form.get('dataSources') as FormArray;
  }
}
