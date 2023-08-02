import { Component } from '@angular/core';
import { Route } from 'src/app/constants/Route';

@Component({
  selector: 'app-page-not-found',
  templateUrl: './page-not-found.component.html',
  styleUrls: ['./page-not-found.component.css']
})
export class PageNotFoundComponent {
  readonly Route = Route;
}
