import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BackLinkService } from '../../../shared/back-link/back-link.service';

@Component({
  selector: 'app-calculating-co2-co2e-details',
  templateUrl: './calculating-co2-co2e-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalculatingCo2Co2eDetailsComponent implements OnInit {
  constructor(private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
