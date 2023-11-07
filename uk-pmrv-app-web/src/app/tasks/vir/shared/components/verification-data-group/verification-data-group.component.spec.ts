import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { VirTaskSharedModule } from '@tasks/vir/shared/vir-task-shared.module';
import { mockVirApplicationSubmitPayload } from '@tasks/vir/submit/testing/mock-vir-application-submit-payload';
import { BasePage } from '@testing';

import { VerificationDataGroupComponent } from './verification-data-group.component';

describe('VerificationDataGroupComponent', () => {
  let page: Page;
  let component: VerificationDataGroupComponent;
  let fixture: ComponentFixture<VerificationDataGroupComponent>;

  class Page extends BasePage<VerificationDataGroupComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, ul a, ul strong').map((item) => item.textContent.trim());
    }

    get verificationDataItem() {
      return this.queryAll<HTMLElement>('app-verification-data-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerificationDataGroupComponent],
      imports: [VirSharedModule, VirTaskSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(VerificationDataGroupComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.virPayload = {
      ...mockVirApplicationSubmitPayload,
      virSectionsCompleted: { A1: true, A2: false, B1: false, C1: true, D1: true },
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual([
      'B1: an uncorrected error in the monitoring plan',
      'Respond to recommendation',
      'in progress',
      'D1: recommended improvement',
      'Respond to recommendation',
      'completed',
      'E1: an unresolved breach from a previous year',
      'Respond to recommendation',
      'not started',
    ]);
    expect(page.verificationDataItem).toHaveLength(3);
  });
});
