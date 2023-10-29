import {Component} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {
  REGISTRATION_TYPE_CUSTOMER,
  RegistrationComponent
} from 'src/app/registration-login/registration/registration.component';
import {AuthService} from "../../auth.service";
import {Router} from "@angular/router";


@Component({
  selector: 'app-public-header',
  templateUrl: './public-header.component.html',
  styleUrls: ['./public-header.component.css']
})
export class PublicHeaderComponent {
  registrationType = REGISTRATION_TYPE_CUSTOMER;

  constructor(private dialog: MatDialog, private authService: AuthService, private router: Router) {
  }

  openSignUpDialog() {
    this.dialog.open(RegistrationComponent, {
      width: '500px',
      height: '500px',
      data: {'registrationType': this.registrationType}
    });
  }

  isUserLoggedIn() {
    console.log('isUserLoggedIn called, returning: ', this.authService.isLoggedIn());
    return this.authService.isLoggedIn();
  }

  logout() {
    this.authService.logout(() => {
      this.router.navigateByUrl('/');
    });
  }

}
