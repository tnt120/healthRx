import { Component, input, output } from '@angular/core';
import { UserDrugMonitorResponse } from '../../../../core/models/user-drug-monitor-response.model';
import { getPriorityName, Priority } from '../../../../core/enums/priority.enum';

@Component({
  selector: 'app-drug-item',
  templateUrl: './drug-item.component.html',
  styleUrl: './drug-item.component.scss'
})
export class DrugItemComponent {
  userDrug = input.required<UserDrugMonitorResponse>();

  setDrugMonitor = output<UserDrugMonitorResponse>();

  editDrugMonitor = output<UserDrugMonitorResponse>();

  isOverflow(e: HTMLElement): boolean {
    return e.scrollWidth <= e.clientWidth;
  }

  getPriorityName(priority: Priority): string {
    return getPriorityName(priority);
  }

  onSet(): void {
    this.setDrugMonitor.emit(this.userDrug());
  }

  onEdit(): void {
    this.editDrugMonitor.emit(this.userDrug());
  }
}
