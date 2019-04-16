import { StarSystem } from '../../../game-objects/star-system';
import { TextService } from './../../../services/text.service';
import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { ThreeService } from 'src/app/services/three.service';

@Component({
  selector: 'app-star-planets',
  templateUrl: './star-planets.component.html',
  styleUrls: ['./star-planets.component.css']
})
export class StarPlanetsComponent implements OnInit {

  @ViewChild('canvas') canvas: ElementRef;

  @Input() starSystem: StarSystem;


  constructor(public ts: TextService, private th: ThreeService) { }

  ngOnInit() {
    this.th.setup(this.canvas.nativeElement);
  }


}
