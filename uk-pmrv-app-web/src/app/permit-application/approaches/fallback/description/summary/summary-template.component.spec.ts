import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { BasePage } from '@testing';

import { FallbackMonitoringApproach } from 'pmrv-api';

import { PermitApplicationModule } from '../../../../permit-application.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockState } from '../../../../testing/mock-state';
import { SummaryTemplateComponent } from './summary-template.component';

describe('SummaryTemplateComponent', () => {
  let component: SummaryTemplateComponent;
  let fixture: ComponentFixture<SummaryTemplateComponent>;
  let store: PermitApplicationStore<PermitApplicationState>;
  let page: Page;

  class Page extends BasePage<SummaryTemplateComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, SharedPermitModule],
      providers: [
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
    fixture = TestBed.createComponent(SummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    const fallbackApproachData = mockPermitApplyPayload.permit.monitoringApproaches
      .FALLBACK as FallbackMonitoringApproach;

    expect(page.summaryListValues).toEqual([
      ['Approach description', fallbackApproachData.approachDescription],
      ['Justification', fallbackApproachData.justification],
    ]);
  });
});
