import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../shared.module';
import { RaSummaryTemplateComponent } from './ra-summary-template.component';

describe('RaSummaryTemplateComponent', () => {
  let page: Page;
  let component: RaSummaryTemplateComponent;
  let fixture: ComponentFixture<RaSummaryTemplateComponent>;

  class Page extends BasePage<RaSummaryTemplateComponent> {
    get returnOfAllowances() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(RaSummaryTemplateComponent);
    component = fixture.componentInstance;
    component.payload = {
      returnOfAllowances: {
        numberOfAllowancesToBeReturned: 10,
        years: [2020, 2021],
        reason: 'Excess allowances',
        dateToBeReturned: '2024-01-01',
        regulatorComments: 'Please return the allowances as soon as possible',
      },
    };

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.returnOfAllowances).toHaveLength(4);
    expect(page.returnOfAllowances).toEqual([
      ['Number of allowances to be returned', ''],
      ['Which years are these allowances being returned for?', ''],
      ['Reason for return of allowances', ''],
      ['Date allowances to be returned by', ''],
    ]);
  });
});
