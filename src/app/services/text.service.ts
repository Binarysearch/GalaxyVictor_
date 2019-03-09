import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TextService {

  private _strings = {
    galaxy: 'Galaxia',
    star: 'Estrella',
    starSystem: 'Sistema estelar',
    colony: 'Colonia',
    colonies: 'Colonias',
    fleets: 'Flotas',
    planets: 'Planetas',
    knownPlanets: 'Planetas conocidos',
    commerce: 'Comercio',
    research: 'Investigación',
    logout: 'Cerrar sesión',
    register: 'Crear una cuenta',
    login: 'Iniciar Sesión',
    password: 'Contraseña',
    repeatPassword: 'Repetir Contraseña',
    email: 'Email',
    name: 'Nombre',
    send: 'Enviar',
    home: 'Inicio',
    type: 'Tipo',
    size: 'Tamaño',
    orbit: 'Orbita',
    loading: 'Cargando',
    viewPlanetInMap: 'Ver planeta en el mapa',
    viewColonyInMap: 'Ver colonia en el mapa',
    viewFleetInMap: 'Ver flota en el mapa',
    createCivilization: 'Crear civilización',
    homeworld: 'Mundo natal',
    civilization: 'Civilización',
    viewCivilization: 'Ver civilización',
    civilizations: 'Civilizaciones',
    notColonized: 'Sin colonizar',
    colonized: 'Colonizado',
    origin: 'Origen',
    destination: 'Destino',
    location: 'Localización',
    travellingFrom: 'Viajando desde',
    to: 'a',
    orbiting: 'Orbitando',
    civilizationName: 'Nombre de la civilización',
    invalidCivilizationName: 'Nombre de civilización invalido: debe tener al menos 3 caracteres y no empezar ni acabar con espacio.',
    invalidHomeStarName: 'Nombre de estrella invalido: debe tener al menos 3 caracteres y no empezar ni acabar con espacio.',
    civilizationNameExists: 'Ese nombre de civilización ya existe en esta galaxia',
    homeStarName: 'Nombre de la estrella inicial',
    selectGalaxy: 'Seleccionar galaxia',
    invalidLoginCredentials: 'Email o Contraseña incorrecta',
    passwordsDontMatch: 'Las contraseñas no coinciden',
    unknown: 'Desconocido',
    na: 'N/A'
  };

  constructor() { }

  get strings() {
    return this._strings;
  }
}
