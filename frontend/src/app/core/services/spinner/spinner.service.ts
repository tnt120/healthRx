import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SpinnerService {
  private loadingSubject = new BehaviorSubject<boolean>(false);

  private loadingCounter = 0;

  getLoadingState() {
    return this.loadingSubject.asObservable();
  }

  loadingOn() {
    if (this.loadingCounter === 0) {
      setTimeout(() => {
        this.loadingSubject.next(true);
      }, 0);
    }
    this.loadingCounter++;
  }

  loadingOff() {
    if (this.loadingCounter > 0) {
      this.loadingCounter--;

    }
    if (this.loadingCounter === 0) {
      setTimeout(() => {
        this.loadingSubject.next(false);
      })
    }
  }
}
