import { StarSystem } from '../../../game-objects/star-system';
import { TextService } from './../../../services/text.service';
import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-star-planets',
  templateUrl: './star-planets.component.html',
  styleUrls: ['./star-planets.component.css']
})
export class StarPlanetsComponent implements OnInit {

  @Input() starSystem: StarSystem;


  constructor(public ts: TextService) { }

  ngOnInit() {
  }


}
