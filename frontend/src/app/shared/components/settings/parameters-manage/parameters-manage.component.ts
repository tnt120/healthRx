import { Component, inject, input, model, OnDestroy, OnInit, signal } from '@angular/core';
import { UserParameterResponse } from '../../../../core/models/user-parameter-response.model';
import { Store } from '@ngrx/store';
import { Parameter } from '../../../../core/models/parameter.model';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ParametersManageDialogComponent, ParametersManageDialogData } from './parameters-manage-dialog/parameters-manage-dialog.component';
import { BreakpointObserver } from '@angular/cdk/layout';
import { ParametersService } from '../../../../core/services/parameters/parameters.service';
import { userParametersActions } from '../../../../core/state/user-parameters/user-parameters.actions';

@Component({
  selector: 'app-parameters-manage',
  templateUrl: './parameters-manage.component.html',
  styleUrl: './parameters-manage.component.scss'
})
export class ParametersManageComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store);

  private readonly dialog = inject(MatDialog);

  private readonly observer = inject(BreakpointObserver);

  private readonly parametersService = inject(ParametersService);

  userParameters = model.required<UserParameterResponse[] | null>();

  parameters = input.required<Parameter[] | null>();

  subscriptions: Subscription[] = [];

  isMobile = signal<boolean>(false);

  ngOnInit(): void {
    this.observer.observe('(max-width: 768px)').subscribe(res => {
      this.isMobile.set(res.matches);
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  onEdit() {
    const data: ParametersManageDialogData = { userParameters: this.userParameters()?.map(up => up.parameter)!, parameters: this.parameters()! };

    const widthString = this.isMobile() ? '100%' : '738px';

    const dialogRef = this.dialog.open(ParametersManageDialogComponent, { data, maxWidth: widthString, width: widthString });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(res => {
        if (res) {
          this.parametersService.editUserParameters(res).subscribe(resp => {
            this.userParameters.set(resp);
            this.store.dispatch(userParametersActions.updateSuccess({ userParameters: resp }));
          })
        }
      })
    )
  }
}
