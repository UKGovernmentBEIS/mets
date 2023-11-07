import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { IdentifiedChangesListComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/identified-changes-list.component';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('IdentifiedChangesListComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: IdentifiedChangesListComponent;
  let fixture: ComponentFixture<IdentifiedChangesListComponent>;

  class Page extends BasePage<IdentifiedChangesListComponent> {
    get items() {
      return this.queryAll<HTMLTableRowElement>('tbody tr')
        .map((row) => Array.from(row.querySelectorAll('td')))
        .map((row) => row.map((cell) => cell.textContent.trim()));
    }

    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Continue',
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(IdentifiedChangesListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show details', () => {
    expect(page.items).toEqual([['B1', 'Explanation B1', 'Change', 'Remove']]);
  });

  it('should navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
  });
});
