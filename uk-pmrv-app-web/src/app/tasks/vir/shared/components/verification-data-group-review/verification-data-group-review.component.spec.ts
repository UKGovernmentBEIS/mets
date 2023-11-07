import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { mockVirApplicationReviewPayload } from '@tasks/vir/review/testing/mock-vir-application-review-payload';
import { VirTaskSharedModule } from '@tasks/vir/shared/vir-task-shared.module';
import { BasePage } from '@testing';

import { VerificationDataGroupReviewComponent } from './verification-data-group-review.component';

describe('VerificationDataGroupReviewComponent', () => {
  let page: Page;
  let component: VerificationDataGroupReviewComponent;
  let fixture: ComponentFixture<VerificationDataGroupReviewComponent>;

  class Page extends BasePage<VerificationDataGroupReviewComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, ul a, ul strong').map((item) => item.textContent.trim());
    }

    get operatorResponseDataItem() {
      return this.queryAll<HTMLElement>('app-operator-response-data-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerificationDataGroupReviewComponent],
      imports: [VirSharedModule, VirTaskSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(VerificationDataGroupReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.virPayload = {
      ...mockVirApplicationReviewPayload,
      reviewSectionsCompleted: {
        B1: false,
        D1: true,
      },
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual([
      'B1: an uncorrected error in the monitoring plan',
      'Respond to operator',
      'in progress',
      'D1: recommended improvement',
      'Respond to operator',
      'completed',
      'E1: an unresolved breach from a previous year',
      'Respond to operator',
      'not started',
    ]);
    expect(page.operatorResponseDataItem).toHaveLength(3);
  });
});
