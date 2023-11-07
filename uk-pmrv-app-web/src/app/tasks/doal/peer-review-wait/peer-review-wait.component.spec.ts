import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { SharedModule } from '@shared/shared.module';
import { SubmitSectionListComponent } from '@tasks/doal/shared/components/submit-section-list/submit-section-list.component';
import { mockDoalPeerWaitReviewResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TaskSharedModule } from '../../shared/task-shared-module';
import { PeerReviewWaitComponent } from './peer-review-wait.component';

describe('PeerReviewWaitComponent', () => {
  let component: PeerReviewWaitComponent;
  let store: CommonTasksStore;
  let page: Page;
  let fixture: ComponentFixture<PeerReviewWaitComponent>;

  class Page extends BasePage<PeerReviewWaitComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get warningTextContents() {
      return this.query('.govuk-warning-text').textContent.trim();
    }

    get appSubmitSectionList() {
      return this.query('app-submit-section-list');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PeerReviewWaitComponent, SubmitSectionListComponent],
      providers: [KeycloakService, ItemNamePipe],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: mockDoalPeerWaitReviewResponseRequestTaskTaskItem,
    });
    fixture = TestBed.createComponent(PeerReviewWaitComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display HTML elements', () => {
    expect(page.warningTextContents).toEqual('!WarningWaiting for peer review, you cannot make any changes');
    expect(page.appSubmitSectionList).toBeTruthy();
  });
});
