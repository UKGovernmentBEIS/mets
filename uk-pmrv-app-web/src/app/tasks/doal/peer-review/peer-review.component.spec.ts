import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { SharedModule } from '@shared/shared.module';
import { SubmitSectionListComponent } from '@tasks/doal/shared/components/submit-section-list/submit-section-list.component';
import { mockDoalPeerReviewResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { PeerReviewComponent } from './peer-review.component';

describe('PeerReviewComponent', () => {
  let component: PeerReviewComponent;
  let store: CommonTasksStore;
  let page: Page;
  let fixture: ComponentFixture<PeerReviewComponent>;

  class Page extends BasePage<PeerReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get notifyOperatorButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[title="Peer review decision"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PeerReviewComponent, SubmitSectionListComponent],
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
      requestTaskItem: mockDoalPeerReviewResponseRequestTaskTaskItem,
    });
    fixture = TestBed.createComponent(PeerReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display HTML elements', () => {
    expect(page.notifyOperatorButton).toBeTruthy();
  });
});
