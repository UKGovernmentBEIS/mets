import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { TimeAllocationCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/time-allocation-corsia-template/time-allocation-corsia-template.component';
import { BasePage } from '@testing';

describe('TimeAllocationCorsiaTemplateComponent', () => {
  let page: Page;
  let component: TimeAllocationCorsiaTemplateComponent;
  let fixture: ComponentFixture<TimeAllocationCorsiaTemplateComponent>;

  class Page extends BasePage<TimeAllocationCorsiaTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TimeAllocationCorsiaTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimeAllocationCorsiaTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
      verificationTotalTime: 'My total time',
      verificationScope: 'My verification scope',
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(2);
    expect(page.summaryValues).toEqual([
      ['Total time of the verification', 'My total time'],
      ['Scope of the verification', 'My verification scope'],
    ]);
  });
});
