import { Component, inject, OnInit, signal } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Activity } from '../../../../core/models/activity.model';
import { UserActivityResponse } from '../../../../core/models/user-activity-response.model';
import { UserActivityRequest } from '../../../../core/models/user-activity-request.model';

export interface ManageUserActivityData {
  activity: Activity | null;
  userActivity: UserActivityResponse | null;
  activities: Activity[];
}

interface currActivity {
  activity: Activity | null;
  activityTime: Date | null;
  duration: number | null;
  averageHeartRate: number | null;
  caloriesBurned: number | null;
}

@Component({
  selector: 'app-manage-user-activity-dialog',
  templateUrl: './manage-user-activity-dialog.component.html',
  styleUrl: './manage-user-activity-dialog.component.scss',
})
export class ManageUserActivityDialogComponent implements OnInit {
  data: ManageUserActivityData = inject(MAT_DIALOG_DATA);

  currData = signal<currActivity>({
    activity: null,
    activityTime: null,
    duration: null,
    averageHeartRate: null,
    caloriesBurned: null
  });

  time = signal<{ hour: string, minute: string }>({
    hour: '12',
    minute: '00'
  });

  caloriesCalculatorEnable = signal<boolean>(false);

  weight = signal<number>(1);

  hours: string[] = [];

  minutes: string[] = [];

  ngOnInit(): void {
    this.generateHoursAndMinutes();

    if (this.data.userActivity) {
      this.currData.set({...this.data.userActivity});

      this.time.set({
        hour: this.hours[this.data.userActivity.activityTime.getHours()],
        minute: this.minutes[this.data.userActivity.activityTime.getMinutes()]
      });
    }

    if (this.data.activity) {
      this.currData.set({ ...this.currData(), activity: this.data.activity });
    }
  }

  getData(): UserActivityRequest | null {
    if (!this.isValid()) return null;

    const date = new Date(
      this.currData().activityTime!.getFullYear(),
      this.currData().activityTime!.getMonth(),
      this.currData().activityTime!.getDate(),
      +this.time().hour + 2,
      +this.time().minute
    );

    return {
      activityId: this.currData()!.activity!.id,
      activityTime: date,
      duration: this.currData()!.duration!,
      averageHeartRate: this.currData()?.averageHeartRate || null,
      caloriesBurned: this.currData()?.caloriesBurned || null
    }
  }

  isValid(): boolean {
    return !!this.currData()?.activity && !!this.currData()?.activityTime && !!this.currData()?.duration && this.currData().duration! > 0;
  }

  private generateHoursAndMinutes() {
    this.hours = Array.from({ length: 24 }, (v, k) => k < 10 ? '0' + k : k.toString());
    this.minutes = Array.from({ length: 60 }, (v, k) => k < 10 ? '0' + k : k.toString());
  }

  toggleCaloriesCalc() {
    this.caloriesCalculatorEnable.set(!this.caloriesCalculatorEnable());
  }

  calcCalories(): void {
    if (this.caloriesCalculatorEnable()) {
      if (this.currData().activity && this.currData().duration && this.weight() && this.weight() > 0) {
        this.currData.set({...this.currData(),
          caloriesBurned: Math.round((this.currData().activity!.metFactor * 3.5 * this.weight() * this.currData().duration!) / 200)
        });
      } else {
        this.currData.set({...this.currData(), caloriesBurned: null});
      }
    }
  }
}
