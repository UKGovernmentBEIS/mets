import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockStateReview } from '@tasks/vir/review/testing/mock-vir-application-review-payload';
import { VirTaskSharedModule } from '@tasks/vir/shared/vir-task-shared.module';
import { BasePage } from '@testing';

import { VirTaskComponent } from './vir-task.component';

describe('VirTaskComponent', () => {
  let page: Page;
  let component: VirTaskComponent;
  let store: CommonTasksStore;
  let fixture: ComponentFixture<VirTaskComponent>;

  class Page extends BasePage<VirTaskComponent> {
    get headingContents(): string {
      return this.query<HTMLHeadingElement>('app-page-heading').textContent.trim();
    }

    get link() {
      return this.query<HTMLLinkElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VirTaskComponent],
      imports: [VirSharedModule, VirTaskSharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateReview);
    fixture = TestBed.createComponent(VirTaskComponent);
    component = fixture.componentInstance;
    component.heading = 'Testheading';
    component.returnToLink = '../..';
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.headingContents).toEqual('Testheading');
    expect(page.link.textContent).toEqual('Return to: Review verifier improvement report');
  });
});
