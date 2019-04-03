import { Component, OnInit } from '@angular/core';
import { Store } from '../store';

@Component({
  selector: 'app-research',
  templateUrl: './research.component.html',
  styleUrls: ['./research.component.css']
})
export class ResearchComponent implements OnInit {

  constructor(public store: Store) { }

  ngOnInit() {
  }

}
