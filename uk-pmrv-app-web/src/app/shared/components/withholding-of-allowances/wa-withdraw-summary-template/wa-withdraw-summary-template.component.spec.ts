import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { WaWithdrawSummaryTemplateComponent } from './wa-withdraw-summary-template.component';

describe('WaWithdrawSummaryTemplateComponent', () => {
  let page: Page;
  let component: WaWithdrawSummaryTemplateComponent;
  let fixture: ComponentFixture<WaWithdrawSummaryTemplateComponent>;

  class Page extends BasePage<WaWithdrawSummaryTemplateComponent> {
    get withholdingOfAllowancesDetails() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(WaWithdrawSummaryTemplateComponent);
    component = fixture.componentInstance;
    component.payload = {
      withholdingWithdrawal: {
        reason: 'Reason for withholding allowances',
      },
    };

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.withholdingOfAllowancesDetails).toHaveLength(1);
    expect(page.withholdingOfAllowancesDetails).toEqual([
      ['Why are allowances being returned?', 'Reason for withholding allowances'],
    ]);
  });
});
