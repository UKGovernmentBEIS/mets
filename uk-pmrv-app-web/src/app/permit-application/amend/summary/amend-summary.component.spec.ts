import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { AmendSummaryComponent } from './amend-summary.component';
import { AmendSummaryTemplateComponent } from './amend-summary-template.component';

describe('AmendSummaryComponent', () => {
  let component: AmendSummaryComponent;
  let fixture: ComponentFixture<AmendSummaryComponent>;
  let store: PermitIssuanceStore;
  let page: Page;

  const route = new ActivatedRouteStub({ section: 'fuels' });

  class Page extends BasePage<AmendSummaryComponent> {
    get summaryLists() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AmendSummaryComponent, AmendSummaryTemplateComponent],
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
    store = TestBed.inject(PermitIssuanceStore);
    store.setState({
      ...mockState,
      reviewGroupDecisions: {
        FUELS_AND_EQUIPMENT: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            requiredChanges: [{ reason: 'Changes required' }],
            notes: 'notes',
          },
        },
      },
    });
    fixture = TestBed.createComponent(AmendSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate information', () => {
    expect(page.summaryLists).toHaveLength(2);

    const markAsCompleteSummaryListRows = page.summaryLists[1].querySelectorAll('.govuk-summary-list__row');
    expect(markAsCompleteSummaryListRows).toHaveLength(1);
    expect(markAsCompleteSummaryListRows[0].querySelector('dt').textContent.trim()).toEqual(
      'I have made changes and want to mark this task as complete',
    );
    expect(markAsCompleteSummaryListRows[0].querySelector('dd').textContent.trim()).toEqual('Yes');
  });
});
