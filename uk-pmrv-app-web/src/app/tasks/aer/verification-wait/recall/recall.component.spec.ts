import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';
import { BasePage, MockType } from '@testing';

import { RecallComponent } from './recall.component';

describe('RecallComponent', () => {
  let page: Page;
  let component: RecallComponent;
  let fixture: ComponentFixture<RecallComponent>;
  const aerService: MockType<AerService> = {
    postSubmit: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<RecallComponent> {
    get heading1() {
      return page.query<HTMLHeadingElement>('h1');
    }

    get heading3() {
      return page.query<HTMLHeadingElement>('h3');
    }

    get paragraph() {
      return page.query<HTMLButtonElement>('p.govuk-body');
    }

    get button() {
      return page.queryAll<HTMLButtonElement>('button')[0];
    }

    get link() {
      return page.query<HTMLButtonElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RecallComponent],
      imports: [RouterTestingModule, AerSharedModule, SharedModule],
      providers: [{ provide: AerService, useValue: aerService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RecallComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render initial template of component', () => {
    expect(component.isConfirmed$.value).toBeFalsy();
    expect(page.heading1.textContent.trim()).toEqual('Are you sure you want to recall the report?');
    expect(page.button.textContent.trim()).toEqual('Yes, recall the report');
    expect(page.link.textContent.trim()).toEqual('Return to: Emissions report');
  });

  it('should render confirmation template of component', () => {
    page.button.click();
    fixture.detectChanges();

    expect(aerService.postSubmit).toHaveBeenCalledTimes(1);
    expect(component.isConfirmed$.value).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('The report has been recalled');
    expect(page.heading3.textContent.trim()).toEqual('What happens next');
    expect(page.paragraph.textContent.trim()).toEqual('You can continue to edit the report from your dashboard.');
  });
});
