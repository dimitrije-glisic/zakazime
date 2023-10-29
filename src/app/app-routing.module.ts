import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HowItWorksComponent} from './public-layout/how-it-works/how-it-works.component';
import {LoginComponent} from './registration-login/login/login.component';
import {PublicLayoutComponent} from './public-layout/public-layout/public-layout.component';
import {HomeComponent} from "./home/home.component";

const routes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      {path: 'home', component: HomeComponent},
      {path: 'how-it-works', component: HowItWorksComponent},
      {path: 'login', component: LoginComponent}
    ]
  },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
