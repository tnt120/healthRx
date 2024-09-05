import { Component, inject, OnInit, signal } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DrugPack } from '../../../../core/models/drug-pack.model';

export interface EditDrugStockDialogData {
  amount: number;
  drugPacks: DrugPack[];
  unit: string;
}

@Component({
  selector: 'app-edit-drug-stock-dialog',
  templateUrl: './edit-drug-stock-dialog.component.html',
  styleUrl: './edit-drug-stock-dialog.component.scss'
})
export class EditDrugStockDialogComponent implements OnInit {
  dialogData: EditDrugStockDialogData = inject(MAT_DIALOG_DATA);

  selectedPack = signal<DrugPack | null>(null);

  isPackAdd = signal<boolean>(false);

  numberOfPacks = signal<number>(0);

  firstPackAmount = signal<number>(0);

  realSize = signal<number>(0);

  ngOnInit(): void {
    this.dialogData.drugPacks.sort((a, b) => a.packSize - b.packSize);
  }

  changeFirstPackAmount() {
    this.firstPackAmount.set(this.selectedPack()?.packSize || 0);
  }

  getPackLabel(pack: DrugPack): string {
    if (pack.packType === '' || pack.packType === null) {
      return `Ilość: ${pack.packSize} ${this.dialogData.unit}`;
    }

    return `Typ: ${pack.packType}, Ilość: ${pack.packSize} ${this.dialogData.unit}`;
  }

  getAmount(): number {
    if (this.isPackAdd() && this.isValid()) {
      return this.dialogData.amount + this.selectedPack()!.packSize * this.numberOfPacks() - (this.selectedPack()!.packSize - this.firstPackAmount());
    }

    return this.dialogData.amount;
  }

  isValid(): boolean {
    if (!this.isPackAdd()) return this.dialogData.amount !== null && this.dialogData.amount >= 0;

    return this.dialogData.amount !== null &&
      this.dialogData.amount >= 0 &&
      !!this.selectedPack() &&
      this.numberOfPacks() !== null &&
      this.numberOfPacks() > 0 &&
      this.firstPackAmount() !== null &&
      this.firstPackAmount() >= 0 &&
      this.firstPackAmount() <= this.selectedPack()!.packSize;
  }
}
