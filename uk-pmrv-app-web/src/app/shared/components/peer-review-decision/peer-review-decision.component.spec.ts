import { Location } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { SharedModule } from '../../shared.module';
import { StoreContextResolver } from '../../store-resolver/store-context.resolver';
import { PeerReviewDecisionComponent } from './peer-review-decision.component';

describe('PeerReviewDecisionComponent', () => {
  let component: PeerReviewDecisionComponent;
  let fixture: ComponentFixture<PeerReviewDecisionComponent>;

  let route: ActivatedRouteStub;

  const storeResolver = mockClass(StoreContextResolver);
  let location: Location;
  let page: Page;

  class Page extends BasePage<PeerReviewDecisionComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub({ taskId: '237' });

    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: StoreContextResolver, useValue: storeResolver },
      ],
    }).compileComponents();

    location = TestBed.inject(Location);
  });

  beforeEach(() => {
    jest.spyOn(location, 'path').mockReturnValue('/permit-issuance/237/review/peer-review-decision');
    storeResolver.getRequestTaskType.mockReturnValue(of('PERMIT_ISSUANCE_APPLICATION_REVIEW'));

    fixture = TestBed.createComponent(PeerReviewDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form and navigate to summary', () => {
    expect(page.errorSummary).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryLinks).toEqual(['Enter your decision', 'Enter notes']);
    expect(location.path).toHaveBeenCalled();
  });
});
