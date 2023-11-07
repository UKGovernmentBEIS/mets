import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { AirTaskSharedModule } from '@tasks/air/shared/air-task-shared.module';
import { mockState } from '@tasks/air/submit/testing/mock-air-application-submit-payload';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { AirTaskComponent } from './air-task.component';

describe('AirTaskComponent', () => {
  let page: Page;
  let component: AirTaskComponent;
  let store: CommonTasksStore;
  let fixture: ComponentFixture<AirTaskComponent>;

  class Page extends BasePage<AirTaskComponent> {
    get headingContents(): string {
      return this.query<HTMLHeadingElement>('app-page-heading').textContent.trim();
    }

    get link() {
      return this.query<HTMLLinkElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AirTaskComponent],
      imports: [AirSharedModule, AirTaskSharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(AirTaskComponent);
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
    expect(page.link.textContent).toEqual('Return to: Annual improvement report');
  });
});
