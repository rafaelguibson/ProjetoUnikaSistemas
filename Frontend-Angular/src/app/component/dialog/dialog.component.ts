import { Component } from '@angular/core';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrl: './dialog.component.css',
  standalone: true,
  imports: [MatButtonModule, MatDialogActions, MatDialogClose, MatDialogTitle, MatDialogContent,FormsModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule],
})
export class DialogComponent {
  constructor(public dialogRef: MatDialogRef<DialogComponent>) {}

  emailFormControl = new FormControl('', [Validators.required, Validators.email]);
}
