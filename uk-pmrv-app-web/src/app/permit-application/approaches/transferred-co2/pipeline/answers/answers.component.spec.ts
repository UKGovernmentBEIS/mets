import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../../shared/shared.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockState } from '../../../../testing/mock-state';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  let page: Page;
  let store: PermitIssuanceStore;
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.TRANSFERRED_CO2_N2O.transportCO2AndN2OPipelineSystems',
  });

  class Page extends BasePage<AnswersComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AnswersComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the pipeline systems', () => {
    const mockPipeline = (
      mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach
    ).transportCO2AndN2OPipelineSystems;

    expect(page.summaryListValues).toEqual([
      ['Are you using any pipeline systems to transport CO2 or N20?', mockPipeline.exist],

      ['Procedure description', mockPipeline.procedureForLeakageEvents.procedureForm.procedureDescription],
      ['Procedure document', mockPipeline.procedureForLeakageEvents.procedureForm.procedureDocumentName],
      ['Procedure reference', mockPipeline.procedureForLeakageEvents.procedureForm.procedureReference],
      [
        'Department or role that’s responsible',
        mockPipeline.procedureForLeakageEvents.procedureForm.responsibleDepartmentOrRole,
      ],
      ['Location of records', mockPipeline.procedureForLeakageEvents.procedureForm.locationOfRecords],
      ['Procedure description', mockPipeline.proceduresForTransferredCO2AndN2O.procedureDescription],
      ['Procedure document', mockPipeline.proceduresForTransferredCO2AndN2O.procedureDocumentName],
      ['Procedure reference', mockPipeline.proceduresForTransferredCO2AndN2O.procedureReference],
      [
        'Department or role that’s responsible',
        mockPipeline.proceduresForTransferredCO2AndN2O.responsibleDepartmentOrRole,
      ],
      ['Location of records', mockPipeline.proceduresForTransferredCO2AndN2O.locationOfRecords],
    ]);
  });
});
