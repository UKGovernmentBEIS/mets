import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLinkWithHref } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import { SelectComponent, TextareaComponent } from 'govuk-components';

import { AviationAccountReportingStatusService, AviationAccountsService, AviationAccountUpdateService } from 'pmrv-api';

import { AviationAccountsStore } from '../../store';
import { EditReportingStatusComponent } from './edit-reporting-status.component';

describe('EditReportingStatusComponent', () => {
  let fixture: ComponentFixture<EditReportingStatusComponent>;
  let store: AviationAccountsStore;
  const reportingStatusService: Partial<AviationAccountReportingStatusService> = {
    submitReportingStatus: jest.fn(),
  };
  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterLinkWithHref],
      declarations: [
        EditReportingStatusComponent,
        PageHeadingComponent,
        WizardStepComponent,
        SelectComponent,
        TextareaComponent,
      ],
      providers: [
        AviationAccountsStore,
        { provide: AviationAccountsService, useValue: mockClass(AviationAccountsService) },
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
        { provide: AviationAccountReportingStatusService, useValue: reportingStatusService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(AviationAccountsStore);
    store.setCurrentAccount({
      aviationAccount: {
        id: 1,
        reportingStatus: 'REQUIRED_TO_REPORT',
        reportingStatusReason: 'TEST',
      } as any,
    });
    fixture = TestBed.createComponent(EditReportingStatusComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display reporting status field', () => {
    expect(screen.getByLabelText(/Reporting status/)).toBeVisible();
  });

  it('should display reporting status reason field', () => {
    expect(screen.getByLabelText(/Reason/)).toBeVisible();
  });
});
