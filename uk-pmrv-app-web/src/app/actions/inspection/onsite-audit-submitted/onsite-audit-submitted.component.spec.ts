import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { InstallationInspectionApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { InspectionActionModule } from '../inspection-action.module';
import { mockStateRegulator } from '../testing/inspection-submitted';
import { OnsiteAuditSubmittedComponent } from './onsite-audit-submitted.component';

describe('OnsiteAuditSubmittedComponent', () => {
  let component: OnsiteAuditSubmittedComponent;
  let fixture: ComponentFixture<OnsiteAuditSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<OnsiteAuditSubmittedComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InspectionActionModule, OnsiteAuditSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateRegulator);

    fixture = TestBed.createComponent(OnsiteAuditSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details for inspection submitted by regulator with follow up required true', () => {
    expect(page.heading).toEqual('On-site inspection submitted to Operator by Regulator');
    expect(page.summaryListValues).toHaveLength(11);
    expect(page.summaryListValues).toEqual([
      ['On-site inspection date', '11 November 2023'],
      ['Names of officers', '1. regulator england'],
      ['Uploaded files (visible to the Operator)', 'decision.PNG'],
      ['Additional information', '123'],
      ['Do you want to add follow-up actions for the operator?', 'Yes'],
      ['Action type', 'Non-conformity'],
      ['Explanation', '111'],
      ['Uploaded files', 'baseline.PNG'],
      ['Users', 'operator17 englandnew, Operator admin - Service contact, Primary contact, Financial contact'],
      ['Name and signature on the official notice', 'regulator england'],
      ['Official notice', 'INSTALLATION_ONSITE_INSPECTION_notice.pdf'],
    ]);
  });

  it('should show summary details for inspection submitted by regulator with follow up required false', () => {
    store.setState({
      ...mockStateRegulator,
      action: {
        ...mockStateRegulator.action,
        payload: {
          ...mockStateRegulator.action.payload,
          installationInspection: {
            ...(mockStateRegulator.action.payload as InstallationInspectionApplicationSubmittedRequestActionPayload)
              .installationInspection,
            followUpActionsRequired: false,
            followUpActionsOmissionJustification: 'text',
            followUpActions: [],
          },
        } as InstallationInspectionApplicationSubmittedRequestActionPayload,
      },
    });
    fixture.detectChanges();

    expect(page.heading).toEqual('On-site inspection submitted to Operator by Regulator');
    expect(page.summaryListValues).toHaveLength(9);
    expect(page.summaryListValues).toEqual([
      ['On-site inspection date', '11 November 2023'],
      ['Names of officers', '1. regulator england'],
      ['Uploaded files (visible to the Operator)', 'decision.PNG'],
      ['Additional information', '123'],
      ['Do you want to add follow-up actions for the operator?', 'No'],
      ['Justification', 'text'],
      ['Users', 'operator17 englandnew, Operator admin - Service contact, Primary contact, Financial contact'],
      ['Name and signature on the official notice', 'regulator england'],
      ['Official notice', 'INSTALLATION_ONSITE_INSPECTION_notice.pdf'],
    ]);
  });
});
