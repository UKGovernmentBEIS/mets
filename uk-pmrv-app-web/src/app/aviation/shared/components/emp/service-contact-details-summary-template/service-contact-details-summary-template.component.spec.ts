import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ServiceContactDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp/service-contact-details-summary-template/service-contact-details-summary-template.component';
import { BasePage } from '@testing';

describe('ServiceContactDetailsSummaryTemplateComponent', () => {
  let page: Page;
  let component: ServiceContactDetailsSummaryTemplateComponent;
  let fixture: ComponentFixture<ServiceContactDetailsSummaryTemplateComponent>;

  class Page extends BasePage<ServiceContactDetailsSummaryTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServiceContactDetailsSummaryTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceContactDetailsSummaryTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
      name: 'FirstName LastName',
      email: 'test@xx.gr',
      roleCode: 'operator_admin',
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(3);
    expect(page.summaryValues).toEqual([
      ['Full name', 'FirstName LastName'],
      ['Role', 'Operator admin'],
      ['Email address', 'test@xx.gr'],
    ]);
  });
});
