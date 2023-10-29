import {Injectable, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {HowItWorksComponent} from './public-layout/how-it-works/how-it-works.component';
import {AppRoutingModule} from './app-routing.module';
import {
  HTTP_INTERCEPTORS,
  HttpClientModule,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RegistrationComponent} from './registration-login/registration/registration.component';
import {LoginComponent} from './registration-login/login/login.component';
import {PublicLayoutComponent} from './public-layout/public-layout/public-layout.component';
import {PublicHeaderComponent} from './public-layout/public-header/public-header.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatDialogModule} from '@angular/material/dialog';
import {FinishRegistrationComponent} from './registration-login/finish-registration/finish-registration.component';
import {AuthService} from "./auth.service";
import { HomeComponent } from './home/home.component';


@Injectable()
export class XhrInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const xhr = req.clone({
      headers: req.headers.set('X-Requested-With', 'XMLHttpRequest')
    });
    return next.handle(xhr);
  }
}

@NgModule({
  declarations: [
    AppComponent,
    HowItWorksComponent,
    RegistrationComponent,
    LoginComponent,
    PublicLayoutComponent,
    PublicHeaderComponent,
    FinishRegistrationComponent,
    HomeComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatDialogModule
  ],
  providers: [AuthService, {provide: HTTP_INTERCEPTORS, useClass: XhrInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
