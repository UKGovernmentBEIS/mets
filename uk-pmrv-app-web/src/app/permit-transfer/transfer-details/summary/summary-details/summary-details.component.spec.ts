import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { mockPermitTransferDetailsConfirmation } from '../../../testing/mock';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<SummaryDetailsComponent>;
  let page: Page;

  class Page extends BasePage<SummaryDetailsComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelectorAll('dd')[0]])
        .map((pair) => pair.map((element) => element.textContent.replace('  ', ' ').trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryDetailsComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(SummaryDetailsComponent);
    component = fixture.componentInstance;
    component.transferDetailsConfirmation = mockPermitTransferDetailsConfirmation;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    expect(page.summaryListValues).toEqual([
      ['Consent with the information provided', 'Yes'],
      ['Regulated activities in operation', 'Yes'],
      [
        'Consent that I will be the operator of the installation and transferres emissions equipment',
        'No Transfer rejected reason',
      ],
    ]);
  });
});
