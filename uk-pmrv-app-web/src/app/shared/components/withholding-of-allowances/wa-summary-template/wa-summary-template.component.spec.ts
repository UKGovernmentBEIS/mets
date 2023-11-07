import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { WaSummaryTemplateComponent } from './wa-summary-template.component';

describe('WaSummaryTemplateComponent', () => {
  let page: Page;
  let component: WaSummaryTemplateComponent;
  let fixture: ComponentFixture<WaSummaryTemplateComponent>;

  class Page extends BasePage<WaSummaryTemplateComponent> {
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

    fixture = TestBed.createComponent(WaSummaryTemplateComponent);
    component = fixture.componentInstance;
    component.payload = {
      withholdingOfAllowances: {
        year: 2027,
        reasonType: 'DETERMINING_A_SURRENDER_APPLICATION',
        regulatorComments: 'sadhn',
      },
    };

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.withholdingOfAllowancesDetails).toHaveLength(3);
    expect(page.withholdingOfAllowancesDetails).toEqual([
      ['From which year are these allowances being withheld?', '2027'],
      ['Reason for withholding allowances', 'Determining a surrender application'],
      ['Your comments', 'sadhn'],
    ]);
  });
});
