import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import ComplianceMonitoringComponent from './compliance-monitoring.component';

class Page extends BasePage<ComplianceMonitoringComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('ComplianceMonitoringComponent', () => {
  let fixture: ComponentFixture<ComplianceMonitoringComponent>;
  let store: RequestActionStore;
  let page: Page;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, ComplianceMonitoringComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED',
        creationDate: '2023-09-20T12:18:46.714Z',
        payload: {
          verificationReport: {
            complianceMonitoringReportingRules: {
              accuracyCompliant: true,
              accuracyNonCompliantReason: 'reason',
              completenessCompliant: true,
              completenessNonCompliantReason: 'reason',
              consistencyCompliant: true,
              consistencyNonCompliantReason: 'reason',
              comparabilityCompliant: true,
              comparabilityNonCompliantReason: 'reason',
              transparencyCompliant: true,
              transparencyNonCompliantReason: 'reason',
              integrityCompliant: true,
              integrityNonCompliantReason: 'reason',
            },
          },
        } as AerRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(ComplianceMonitoringComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Compliance with monitoring and reporting principles');

    expect(page.summaryValues).toEqual([
      ['Accuracy', 'Compliant'],
      ['Completeness', 'Compliant'],
      ['Consistency', 'Compliant'],
      ['Comparability over time', 'Compliant'],
      ['Transparency', 'Compliant'],
      ['Integrity of methodology', 'Compliant'],
    ]);
  });
});
