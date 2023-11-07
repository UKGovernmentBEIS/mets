import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { EmissionsReductionClaimCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/emissions-reduction-claim-corsia-template/emissions-reduction-claim-corsia-template.component';
import { BasePage } from '@testing';

describe('EmissionsReductionClaimCorsiaTemplateComponent', () => {
  let page: Page;
  let component: EmissionsReductionClaimCorsiaTemplateComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimCorsiaTemplateComponent>;

  class Page extends BasePage<EmissionsReductionClaimCorsiaTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmissionsReductionClaimCorsiaTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmissionsReductionClaimCorsiaTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
      reviewResults: 'My review results',
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(1);
    expect(page.summaryValues).toEqual([['Describe the results of your review', 'My review results']]);
  });
});
