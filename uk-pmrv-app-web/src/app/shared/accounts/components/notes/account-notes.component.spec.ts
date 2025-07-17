import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';

import { AccountNotesService } from 'pmrv-api';

import { mockAccountNotesResults } from '../../../../accounts/testing/mock-data';
import { SharedUserModule } from '../../../../shared-user/shared-user.module';
import { AccountNotesComponent } from './account-notes.component';

describe('NotesComponent', () => {
  let component: AccountNotesComponent;
  let fixture: ComponentFixture<AccountNotesComponent>;
  let page: Page;

  const accountNotesService: MockType<AccountNotesService> = {
    getNotesByAccountId: jest.fn().mockReturnValue(of(mockAccountNotesResults)),
  };
  const activatedRoute = new ActivatedRouteStub({ accountId: '1' });

  class Page extends BasePage<AccountNotesComponent> {
    get notesContent() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelectorAll('dd')[0]])
        .map((pair) => pair.map((element) => element.textContent));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AccountNotesComponent],
      imports: [RouterTestingModule, SharedModule, SharedUserModule],
      providers: [
        { provide: AccountNotesService, useValue: accountNotesService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountNotesComponent);
    component = fixture.componentInstance;
    component.currentTab = 'notes';
    page = new Page(fixture);
    jest.clearAllMocks();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show notes', () => {
    const govukDatePipe = new GovukDatePipe();
    expect(accountNotesService.getNotesByAccountId).toHaveBeenCalledTimes(1);
    expect(accountNotesService.getNotesByAccountId).toHaveBeenLastCalledWith(1, 0, 10);

    expect(page.notesContent).toEqual([
      ['Add a note'],
      [
        `The note 1 file 1 Submitter 1, ${govukDatePipe.transform(
          mockAccountNotesResults.accountNotes[0].lastUpdatedOn,
          'datetime',
        )}`,
      ],
      [
        `The note 2Submitter 2, ${govukDatePipe.transform(
          mockAccountNotesResults.accountNotes[1].lastUpdatedOn,
          'datetime',
        )}`,
      ],
    ]);
  });
});
