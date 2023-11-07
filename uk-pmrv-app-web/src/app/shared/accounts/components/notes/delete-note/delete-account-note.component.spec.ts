import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';

import { AccountNotesService } from 'pmrv-api';

import { DeleteAccountNoteComponent } from './delete-account-note.component';

describe('DeleteNoteComponent', () => {
  let component: DeleteAccountNoteComponent;
  let fixture: ComponentFixture<DeleteAccountNoteComponent>;
  let page: Page;
  let router: Router;

  const activatedRoute = new ActivatedRouteStub({ accountId: 1, noteId: 2 });
  const accountNotesService: MockType<AccountNotesService> = {
    deleteAccountNote: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<DeleteAccountNoteComponent> {
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeleteAccountNoteComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        DestroySubject,
        { provide: AccountNotesService, useValue: accountNotesService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteAccountNoteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should delete', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();

    expect(accountNotesService.deleteAccountNote).toHaveBeenCalledTimes(1);
    expect(accountNotesService.deleteAccountNote).toHaveBeenCalledWith(2);

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith([`accounts/${activatedRoute.snapshot.paramMap.get('accountId')}`], {
      fragment: 'notes',
    });
  });
});
