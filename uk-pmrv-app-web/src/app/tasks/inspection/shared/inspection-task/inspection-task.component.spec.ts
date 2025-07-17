import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { SharedModule } from '@shared/shared.module';
import { inspectionSubmitMockState } from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { InspectionTaskComponent } from './inspection-task.component';

describe('InspectionTaskComponent', () => {
  let page: Page;
  let component: InspectionTaskComponent;
  let store: CommonTasksStore;
  let fixture: ComponentFixture<InspectionTaskComponent>;

  class Page extends BasePage<InspectionTaskComponent> {
    get headingContents(): string {
      return this.query<HTMLHeadingElement>('app-page-heading').textContent.trim();
    }

    get link() {
      return this.query<HTMLLinkElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [TaskTypeToBreadcrumbPipe],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(inspectionSubmitMockState);

    fixture = TestBed.createComponent(InspectionTaskComponent);
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
    expect(page.link.textContent).toEqual('Return to: Create audit report');
  });
});
