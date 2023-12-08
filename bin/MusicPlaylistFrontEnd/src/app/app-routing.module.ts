import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { HomeComponent } from './home/home.component';
import { LoginsComponent } from './logins/logins.component';
const routes: Routes = [
  // Define the route for the "home" component
  {
    path: 'home',
    component: HomeComponent,
  },
{


  path: 'login',
    component: LoginsComponent,
}
];

@NgModule({
  imports: [RouterModule.forRoot(routes), HttpClientModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }
