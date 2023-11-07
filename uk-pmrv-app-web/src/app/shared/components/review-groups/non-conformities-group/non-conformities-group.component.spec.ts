import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NonConformitiesGroupComponent } from '@shared/components/review-groups/non-conformities-group/non-conformities-group.component';
import { SharedModule } from '@shared/shared.module';
import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('NonConformitiesGroupComponent', () => {
  let page: Page;
  let component: NonConformitiesGroupComponent;
  let fixture: ComponentFixture<NonConformitiesGroupComponent>;

  class Page extends BasePage<NonConformitiesGroupComponent> {
    get perPlanGroup() {
      return this.query('app-non-conformities-per-plan-group');
    }

    get previousYearGroup() {
      return this.query('app-non-conformities-previous-year-group');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NonConformitiesGroupComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NonConformitiesGroupComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.uncorrectedNonConformities = mockVerificationApplyPayload.verificationReport.uncorrectedNonConformities;
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all summary elements', () => {
    expect(page.perPlanGroup).toBeTruthy();
    expect(page.previousYearGroup).toBeTruthy();
  });
});
