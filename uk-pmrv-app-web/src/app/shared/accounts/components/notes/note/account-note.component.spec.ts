import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, BasePage, MockType, RouterStubComponent } from '@testing';

import { AccountNotesService } from 'pmrv-api';

import { mockAccountNotesResults } from '../../../../../accounts/testing/mock-data';
import { AccountNoteComponent } from './account-note.component';

describe('AddEditTemplateComponent', () => {
  let component: AccountNoteComponent;
  let fixture: ComponentFixture<AccountNoteComponent>;
  let page: Page;

  const accountId = 1;
  const noteId = 2;
  const activatedRoute = new ActivatedRouteStub();

  const accountNotesService: MockType<AccountNotesService> = {
    createAccountNote: jest.fn().mockReturnValue(asyncData(null)),
    updateAccountNote: jest.fn().mockReturnValue(asyncData(null)),
    getAccountNote: jest.fn().mockReturnValue(asyncData(mockAccountNotesResults.accountNotes[0])),
    uploadAccountNoteFile: jest.fn().mockReturnValue(asyncData({ uuid: '0500d8b5-8cfb-4430-8edd-75f7612a7287' })),
  };

  const runOnPushChangeDetection = async (fixture: ComponentFixture<any>): Promise<void> => {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  };

  class Page extends BasePage<AccountNoteComponent> {
    get title() {
      return this.query<HTMLDivElement>('.govuk-heading-l');
    }
    get noteContent() {
      return this.getInputValue('#note');
    }

    set noteContent(value: string) {
      this.setInputValue('#note', value);
    }

    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }

    set fileValue(value: File) {
      this.setInputValue('input[type="file"]', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLAnchorElement>('.govuk-error-summary li a');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AccountNoteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AccountNoteComponent],
      imports: [
        RouterTestingModule.withRoutes([{ path: 'accounts/:accountId', component: RouterStubComponent }]),
        SharedModule,
      ],
      providers: [
        DestroySubject,
        { provide: AccountNotesService, useValue: accountNotesService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should succesfully create a note', async () => {
    activatedRoute.setParamMap({ accountId });
    activatedRoute.setResolveMap({
      pageTitle: 'Add a note',
    });
    createComponent();

    await runOnPushChangeDetection(fixture);

    page.submitButton.click();

    await runOnPushChangeDetection(fixture);

    expect(page.title.textContent).toEqual('Add a note');
    expect(page.filesText.map((row) => row.textContent.trim())).toEqual([]);
    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummary.textContent).toContain('Enter a note');

    page.noteContent = 'A note is added';
    page.submitButton.click();
    await runOnPushChangeDetection(fixture);

    expect(page.errorSummary).toBeFalsy();
    expect(accountNotesService.createAccountNote).toHaveBeenCalledTimes(1);
    expect(accountNotesService.createAccountNote).toHaveBeenCalledWith({
      accountId,
      files: [],
      note: 'A note is added',
    });
  });

  it('should succesfully edit a note', async () => {
    activatedRoute.setParamMap({ accountId, noteId });
    activatedRoute.setResolveMap({
      pageTitle: 'Edit a note',
    });
    createComponent();

    await runOnPushChangeDetection(fixture);

    expect(page.title.textContent).toEqual('Edit a note');
    expect(page.noteContent).toEqual('The note 1');

    await fixture.whenStable();
    await runOnPushChangeDetection(fixture);

    expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['file 1']);

    page.noteContent = 'Note has changed';
    page.submitButton.click();

    await runOnPushChangeDetection(fixture);

    expect(page.errorSummary).toBeFalsy();
    expect(accountNotesService.updateAccountNote).toHaveBeenCalledTimes(1);
    expect(accountNotesService.updateAccountNote).toHaveBeenCalledWith(noteId, {
      files: ['0500d8b5-8cfb-4430-8edd-75f7612a7287'],
      note: 'Note has changed',
    });
  });
});
