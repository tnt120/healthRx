import { Component, inject, OnDestroy } from '@angular/core';
import { AdminService } from '../../../../core/services/admin/admin.service';
import { basePagination } from '../../../../core/constants/paginations-options';
import { Pagination } from '../../../../core/models/pagination.model';
import { PageEvent } from '@angular/material/paginator';
import { filter, Observable, Subscription, tap } from 'rxjs';
import { DoctorResponse } from '../../../../core/models/doctor-response.model';
import { DoctorVerificationRequest } from '../../../../core/models/doctor-verification-request.model';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent, ConfirmationDialogData } from '../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { RejectDoctorVerificationDialogComponent } from '../../components/reject-doctor-verification-dialog/reject-doctor-verification-dialog.component';
import { BreakpointObserver } from '@angular/cdk/layout';

@Component({
  selector: 'app-doctor-approvals',
  templateUrl: './doctor-approvals.component.html',
  styleUrl: './doctor-approvals.component.scss'
})
export class DoctorApprovalsComponent implements OnDestroy {
  private readonly adminService = inject(AdminService);

  private readonly observer = inject(BreakpointObserver);

  private readonly dialog = inject(MatDialog);

  pagination: Pagination = {...basePagination };

  doctors$: Observable<DoctorResponse[]> = this.adminService.getApprovalsSubject();

  isLoading$: Observable<boolean> = this.adminService.getLoading();

  subscriptions: Subscription = new Subscription();

  dialogMinWidth = '40vw';

  ngOnInit(): void {
    this.observer.observe('(max-width: 768px)').subscribe(res => {
      res.matches ? this.dialogMinWidth = '95vw' : this.dialogMinWidth = '40vw';
    });

    this.getApprovals();
  }

  getApprovals(): void {
    this.adminService.getApprovals(this.pagination.pageIndex, this.pagination.pageSize).pipe(
      tap(res => {
        this.pagination.totalElements = res.totalElements;
      })
    ).subscribe();
  }

  handlePageEvent(e: PageEvent) {
    this.pagination.pageEvent = e;
    this.pagination.pageSize = e.pageSize;
    this.pagination.pageIndex = e.pageIndex;
    this.getApprovals();
  }

  onApprove(doctor: DoctorResponse): void {
    const data: ConfirmationDialogData = {
      title: 'Weryfikacja lekarza',
      message: 'Czy na pewno chcesz zatwierdzić tego lekarza?',
      acceptButton: 'Weryfikuj'
    }

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, { data });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        filter(res => res),
        tap(() => {
          const req: DoctorVerificationRequest = {
            validVerification: true,
            doctorId: doctor.id,
            message: null
          };

          this.verification(req);
        }),
      ).subscribe()
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  onReject(doctor: DoctorResponse): void {
    const dialogRef = this.dialog.open(RejectDoctorVerificationDialogComponent, { minWidth: this.dialogMinWidth });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        filter(res => res),
        tap((message: string) => {
          const req: DoctorVerificationRequest = {
            validVerification: false,
            doctorId: doctor.id,
            message
          };

          this.verification(req);
        })
      ).subscribe()
    )
  }

  verification(req: DoctorVerificationRequest): void {
    this.adminService.verifyDoctor(req).subscribe();
  }
}
