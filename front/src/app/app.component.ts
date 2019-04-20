import { Component, OnInit } from '@angular/core';
import { CoreService } from './services/core.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  asideMenuVisible = false;

  constructor(private core: CoreService) {}

  closeAsideMenu() {
    this.asideMenuVisible = false;
  }

  toggleAsideMenu() {
    this.asideMenuVisible = !this.asideMenuVisible;
  }

  ngOnInit(): void {
    this.core.auth();
  }

}
