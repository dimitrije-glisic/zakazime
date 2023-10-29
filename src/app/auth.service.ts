import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Business} from './interfaces/business.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  authenticated = false;

  constructor(private http: HttpClient) {
  }

  registerUser(userData: any): Observable<any> {
    return this.http.post('/register', userData)
      .pipe(
        tap((response: any) => {
          if (response) {
            localStorage.setItem('jwtToken', response.token);
          }
        })
      );
  }

  login(credentials: any, callback: any) {
    const headers = new HttpHeaders(credentials ? {
      authorization: 'Basic ' + btoa(credentials.email + ':' + credentials.password)
    } : {});
    this.http.get('user', {headers: headers}).subscribe(response => {
      if ((response as any)['name']) {
        this.authenticated = true;
      } else {
        this.authenticated = false;
      }
      return callback && callback();
    });
  }

  logout(callback: any) {
    this.http.post('logout', {}).subscribe(() => {
      this.authenticated = false;
      return callback && callback();
    });
  }

  finishRegistration(email: string, data: Business) {
    console.log('finish registration called with data: ', data);
    // return this.http.post(`/users/${email}/finish-registration`, data
    //   , {headers: authHeader})
    //   .pipe(
    //     tap((response: any) => {
    //       if (response) {
    //         localStorage.setItem('finishedRegistration', 'true');
    //       }
    //     })
    //   );
  }

  isLoggedIn() {
    console.log('isLoggedIn called, authenticated: ', this.authenticated);
    return this.authenticated;
  }

  isFinishedRegistration() {
    return localStorage.getItem('finishedRegistration') === 'true';
  }

}
