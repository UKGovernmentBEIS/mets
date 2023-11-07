import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { AerModule } from '../../../aer.module';
import { AmendSummaryComponent } from './amend-summary.component';

describe('AmendSummaryComponent', () => {
  let page: Page;
  let component: AmendSummaryComponent;
  let fixture: ComponentFixture<AmendSummaryComponent>;

  class Page extends BasePage<AmendSummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AmendSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryListValues).toEqual([['I have made changes and want to mark this task as complete', 'Yes']]);
  });
});
