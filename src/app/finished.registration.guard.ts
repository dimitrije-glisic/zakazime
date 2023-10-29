import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { CanActivate } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class FinishedRegistrationGuard implements CanActivate {

  constructor(private router: Router, private authService: AuthService) {
  }

  canActivate(): boolean {
    if (this.authService.isFinishedRegistration()) {
      return true;
    }
    const email = this.authService.decodeEmailFromToken();
    this.router.navigate(['finish-registration'], { queryParams: { email: email } });
    return false;
  }

}
