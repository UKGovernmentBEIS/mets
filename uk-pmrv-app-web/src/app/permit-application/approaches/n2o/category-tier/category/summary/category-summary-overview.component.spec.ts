import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SourceStreamPipe } from '@permit-application/emission-summaries/emission-summaries-summary/source-stream.pipe';
import { FindEmissionPointPipe } from '@permit-application/shared/pipes/find-emission-point.pipe';
import { CategoryTypeNamePipe } from '@shared/pipes/category-type-name.pipe';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { TransferredCO2N2ODirectionsPipe } from '@shared/pipes/transferred-co2-n2o-directions.pipe';

import { MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

import { BasePage } from '../../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../../../shared/shared.module';
import { EmissionPointPipe } from '../../../../../shared/pipes/emission-point.pipe';
import { EmissionSourcePipe } from '../../../../../shared/pipes/emission-source.pipe';
import { FindSourceStreamPipe } from '../../../../../shared/pipes/find-source-stream.pipe';
import { TaskPipe } from '../../../../../shared/pipes/task.pipe';
import { TierSourceStreamNamePipe } from '../../../../../shared/pipes/tier-source-stream-name.pipe';
import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockState } from '../../../../../testing/mock-state';
import { CategorySummaryOverviewComponent } from './category-summary-overview.component';

describe('CategorySummaryOverviewComponent', () => {
  let component: CategorySummaryOverviewComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  @Component({
    template: `
      <app-category-summary-overview [emissionPointCategory]="sourceStream"></app-category-summary-overview>
    `,
  })
  class TestComponent {
    sourceStream = (
      mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach
    ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;
  }

  class Page extends BasePage<TestComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, SharedModule],
      declarations: [
        TestComponent,
        SourceStreamPipe,
        FindSourceStreamPipe,
        FindEmissionPointPipe,
        CategoryTypeNamePipe,
        CategorySummaryOverviewComponent,
        EmissionSourcePipe,
        EmissionPointPipe,
        TaskPipe,
        TierSourceStreamNamePipe,
        TransferredCO2N2ODirectionsPipe,
      ],
      providers: [
        SourceStreamDescriptionPipe,
        CategoryTypeNamePipe,
        TransferredCO2N2ODirectionsPipe,
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(CategorySummaryOverviewComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the source stream category summary', () => {
    expect(page.summaryDefinitions).toEqual([
      '13123124 White Spirit & SBP',
      'S1 Boiler',
      'The big Ref Emission point 1',
      'Abated',
      'Calculation',
      '23.5 tonnes',
      'No',
    ]);
  });
});
