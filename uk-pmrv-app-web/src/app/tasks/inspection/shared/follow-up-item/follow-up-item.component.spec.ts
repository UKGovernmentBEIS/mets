import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { FollowUpActionTypePipe } from '../pipes/follow-up-action-type.pipe';
import { FollowUpItemComponent } from './follow-up-item.component';

describe('FollowUpItemComponent', () => {
  let page: Page;
  let component: FollowUpItemComponent;
  let fixture: ComponentFixture<FollowUpItemComponent>;

  class Page extends BasePage<FollowUpItemComponent> {
    get dataSummary() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FollowUpItemComponent, SharedModule, RouterTestingModule, FollowUpActionTypePipe],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(FollowUpItemComponent);
    component = fixture.componentInstance;
  };

  describe('for existing data gaps', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.followUpAction = {
        followUpActionType: 'NON_CONFORMITY',
        explanation: 'The process does not conform to the required standards.',
        followUpActionAttachments: [],
      };

      component.isEditable = true;

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary ', () => {
      expect(page.dataSummary).toHaveLength(2);

      expect(page.dataSummary).toEqual([
        ['Action type', 'Non-conformity'],
        ['Explanation', 'The process does not conform to the required standards.'],
      ]);
    });

    // test onRemoveFollowUpItem
    it('should emit removeFollowUpItem event', () => {
      const spy = jest.spyOn(component.removeFollowUpItem, 'emit');

      component.onRemoveFollowUpItem(1);

      expect(spy).toHaveBeenCalledWith(1);
    });
  });
});
