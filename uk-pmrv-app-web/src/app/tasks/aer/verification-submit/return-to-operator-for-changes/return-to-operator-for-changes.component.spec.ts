import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { ActivatedRouteStub, asyncData, BasePage, MockType } from '@testing';

import { AerVerifyReturnToOperatorForChangesComponent } from './return-to-operator-for-changes.component';

describe('ReturnToOperatorForChangesComponent', () => {
  let component: AerVerifyReturnToOperatorForChangesComponent;
  let fixture: ComponentFixture<AerVerifyReturnToOperatorForChangesComponent>;
  let page: Page;
  let aerService: MockType<AerService>;

  class Page extends BasePage<AerVerifyReturnToOperatorForChangesComponent> {
    get changesRequiredValue() {
      return this.getInputValue('#changesRequired');
    }

    set changesRequiredValue(value: string) {
      this.setInputValue('#changesRequired', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    aerService = {
      postSubmit: jest.fn().mockReturnValue(asyncData({})),
      isEditable$: of(true),
    };

    await TestBed.configureTestingModule({
      imports: [AerVerifyReturnToOperatorForChangesComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ index: '0' }) },
        { provide: AerService, useValue: aerService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AerVerifyReturnToOperatorForChangesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form and display confirmation content', () => {
    expect(page.errorSummary).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList).toEqual(['Enter the changes required']);

    page.changesRequiredValue = 'Test changes';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(aerService.postSubmit).toHaveBeenCalledWith('AER_VERIFICATION_RETURN_TO_OPERATOR', {
      changesRequired: 'Test changes',
    });
  });
});
