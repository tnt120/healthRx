import { Component, input } from '@angular/core';
import { UserDrugMonitorResponse } from '../../../../core/models/user-drug-monitor-response.model';

@Component({
  selector: 'app-drug-item',
  templateUrl: './drug-item.component.html',
  styleUrl: './drug-item.component.scss'
})
export class DrugItemComponent {
  userDrug = input.required<UserDrugMonitorResponse>();

  isOverflow(e: HTMLElement): boolean {
    return e.scrollWidth <= e.clientWidth;
  }
}
