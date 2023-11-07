import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { ReasonListComponent } from '@tasks/aer/verification-submit/overall-decision/reason-list/reason-list.component';
import { mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { VerifiedWithCommentsOverallAssessment } from 'pmrv-api';

describe('ReasonListComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: ReasonListComponent;
  let fixture: ComponentFixture<ReasonListComponent>;

  class Page extends BasePage<ReasonListComponent> {
    get buttons() {
      return this.queryAll<HTMLButtonElement>('.govuk-button-group');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReasonListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  describe('for existing overall decision', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          overallAssessment: {
            type: 'VERIFIED_WITH_COMMENTS',
            reasons: ['First reason', 'Second reason'],
          } as VerifiedWithCommentsOverallAssessment,
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the list', () => {
      expect(page.buttons).toHaveLength(1);
      expect(page.buttons.map((el) => el.textContent.trim())).toEqual(['Continue']);
    });
  });
});
