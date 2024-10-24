import { ConfirmationDialogComponent, ConfirmationDialogData } from './../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { filter, map, Observable, Subscription, tap } from 'rxjs';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { AdminService } from '../../../../core/services/admin/admin.service';
import { AdminParameterResponse } from '../../../../core/models/admin/admin-parameter-response.model';
import { TableColumn } from '../../../../shared/components/table/table.component';
import { Parameter } from '../../../../core/models/parameter.model';
import { MatDialog } from '@angular/material/dialog';
import { ParameterRequest } from '../../../../core/models/admin/parameter-request.model';
import { ParameterMangeDialogComponent, ParameterMangeDialogData } from '../../components/parameter-mange-dialog/parameter-mange-dialog.component';

@Component({
  selector: 'app-parameters-manage',
  templateUrl: './parameters-manage.component.html',
  styleUrl: './parameters-manage.component.scss'
})
export class ParametersManageComponent implements OnInit, OnDestroy {
  private readonly adminService = inject(AdminService);

  private readonly dialog = inject(MatDialog);

  private subscriptions = new Subscription();

  protected parametersAndUnits$?: Observable<AdminParameterResponse>;

  protected isLoading$?: Observable<boolean>;

  parametersAndUnits = signal<AdminParameterResponse | null>(null);

  columns: TableColumn[] = [];

  ngOnInit(): void {
    this.parametersAndUnits$ = this.adminService.getParameters().pipe(
      tap(res => this.parametersAndUnits.set(res))
    );
    this.isLoading$ = this.adminService.getLoading();
    this.fillColumns();
  }

  private fillColumns(): void {
    this.columns = [
      { title: 'Nazwa', displayedColumn: 'name' },
      { title: 'Wskazówka', displayedColumn: 'hint' },
      { title: 'Jednostka', displayedColumn: 'unit' },
      { title: 'Min. wartość normy', displayedColumn: 'minStandardValue' },
      { title: 'Max. wartość normy', displayedColumn: 'maxStandardValue' },
      { title: 'Min. wartość dozwolona', displayedColumn: 'minValue' },
      { title: 'Max. wartość dozwolona', displayedColumn: 'maxValue' },
    ]
  }

  onAdd(): void {
    const data: ParameterMangeDialogData = {
      units: this.parametersAndUnits()?.units || []
    }

    const dialogRef = this.dialog.open(ParameterMangeDialogComponent, { data, minWidth: '350px' });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        filter(res => res),
        tap((res: ParameterRequest) => this.handleParameterAdd(res))
      ).subscribe()
    )
  }

  onEdit(param: Parameter): void {
    const data: ParameterMangeDialogData = {
      parameter: param,
      units: this.parametersAndUnits()?.units || []
    }

    const dialogRef = this.dialog.open(ParameterMangeDialogComponent, { data, minWidth: '350px' });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        filter(res => res),
        map(res => {
          const req: Partial<ParameterRequest> = {
            name: res.name === param.name ? undefined : res.name,
            hint: res.hint === param.hint ? undefined : res.hint,
            unitId: res.unitId === this.parametersAndUnits()!.units.find(unit => unit.symbol === param.unit)!.id ? undefined : res.unitId,
            minStandardValue: res.minStandardValue === param.minStandardValue ? undefined : res.minStandardValue,
            maxStandardValue: res.maxStandardValue === param.maxStandardValue ? undefined : res.maxStandardValue,
            minValue: res.minValue === param.minValue ? undefined : res.minValue,
            maxValue: res.maxValue === param.maxValue ? undefined : res.maxValue
          }

          return req;
        }),
        tap((res) => this.handleParameterEdit(res, param.id))
      ).subscribe()
    )
  }

  onDelete(param: Parameter): void {
    const data: ConfirmationDialogData = {
      title: 'Usuwanie parametru',
      message: `Czy na pewno chcesz usunąć parametr "${param.name}"?`,
      acceptButton: 'Usuń',
    }

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, { data });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        filter(res => res),
        tap(() => this.handleParameterDelete(param))
      ).subscribe()
    )
  }

  private handleParameterAdd(res: ParameterRequest): void {
    this.adminService.addParameter(res).pipe(
      tap(param => {
        this.parametersAndUnits.set({
          parameters: [...(this.parametersAndUnits()?.parameters || []), param],
          units: this.parametersAndUnits()?.units || []
        });
      })
    ).subscribe();
  }

  private handleParameterEdit(res: Partial<ParameterRequest>, id: string): void {
    this.adminService.editParameter(res, id).pipe(
      tap(param => {
        this.parametersAndUnits.set({
          parameters: this.parametersAndUnits()?.parameters.map(p => p.id === param.id ? param : p) || [],
          units: this.parametersAndUnits()?.units || []
        });
      })
    ).subscribe();
  }

  private handleParameterDelete(param: Parameter): void {
    this.adminService.deleteParameter(param.id).pipe(
      tap(() => {
        this.parametersAndUnits.set({
          parameters: this.parametersAndUnits()?.parameters.filter(p => p.id !== param.id) || [],
          units: this.parametersAndUnits()?.units || []
        });
      })
    ).subscribe();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
