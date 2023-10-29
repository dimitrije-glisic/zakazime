import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { REGISTRATION_TYPE_BUSINESS, RegistrationComponent } from 'src/app/registration-login/registration/registration.component';

@Component({
  selector: 'app-how-it-works',
  templateUrl: './how-it-works.component.html',
  styleUrls: ['./how-it-works.component.css']
})
export class HowItWorksComponent {
  private registrationType = REGISTRATION_TYPE_BUSINESS;

  constructor(private dialog: MatDialog) { }

  ngOnInit(): void {
  }
  
  openSignUpDialog() {
    this.dialog.open(RegistrationComponent, {
      width: '500px',
      height: '500px',
      data: {'registrationType': this.registrationType},
    });
  }

}
