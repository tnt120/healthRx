import { SpinnerService } from './core/services/spinner/spinner.service';
import { Component, inject } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  private readonly spinnerService = inject(SpinnerService);

  title = 'healthRx';

  protected isLoading$ = this.spinnerService.getLoadingState();
}
