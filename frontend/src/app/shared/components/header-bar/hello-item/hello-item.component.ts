import { Component, input, OnDestroy, OnInit } from '@angular/core';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-hello-item',
  templateUrl: './hello-item.component.html',
  styleUrl: './hello-item.component.scss'
})
export class HelloItemComponent implements OnInit, OnDestroy {
  name = input<string | null>('');

  isDate = input.required<boolean>();

  currentDate: Date = new Date();

  private timerSubscription: Subscription | undefined;

  ngOnInit(): void {
    if (this.isDate()) {
      this.timerSubscription = interval(300000).subscribe(() => {
        this.currentDate = new Date();
      })
    }
  }
  
  ngOnDestroy(): void {
    this.timerSubscription?.unsubscribe();
  }
}
