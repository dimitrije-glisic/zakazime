import {Component} from '@angular/core';
import {Token} from "../interfaces/token.interface";
import {Greeting} from "../interfaces/greeting.interface";
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  greeting: Greeting = {id: 0, content: ''};

  constructor(private http: HttpClient) {
    http.get<Token>('token').subscribe(data => {
      const token = data['token'];
      http.get<Greeting>('http://localhost:9000', {headers: new HttpHeaders().set('X-Auth-Token', token)})
        .subscribe(response => this.greeting = response);
    }, () => {
    });
  }

}
