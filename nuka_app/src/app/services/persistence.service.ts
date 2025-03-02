import { Injectable } from '@angular/core';
import { AppDataModel } from '../app.data.model';
import { Storage } from '@ionic/storage';

@Injectable({
  providedIn: 'root'
})
export class PersistenceService {

  constructor(
    public storage: Storage,
    private appDataModel: AppDataModel
  ) { }

  async createStorage(): Promise<void> {
    await this.storage.create();
  }

  getToken(): Promise<string> {
    return this.getValue(this.appDataModel.token);
  }

  setToken(value: String ): Promise<boolean> {
    return this.setValue(this.appDataModel.token, value);
  }

  getValue(key: string): Promise<any> {
    return new Promise((resolve) => {
      this.storage.get(key).then(value => {
        try {
          resolve(JSON.parse(value));
        } catch (e) {
          resolve(value);
        }
      }).catch((error) => {
        console.log(error);
        resolve(null);
      });
    });
  }

  setValue(key: string, value: any): Promise<any> {
    return new Promise((resolve) => {
      this.storage.set(key, value).then((data) => {
        resolve(true);
      }).catch((error) => {
        console.log(error);
        resolve(false);
      });
    });
  }

  removeValue(key: string): Promise<boolean> {
    return new Promise((resolve) => {
      this.storage.remove(key).then(_ => {
        resolve(true);
      }).catch((error) => {
        console.log(error);
        resolve(false);
      });
    });
  }

  resetValues(): Promise<any> {
    return new Promise((resolve, reject) => {
      const promises = [];
      promises.push(new Promise((resolve2) => {
        this.storage.clear().then((_: any) => {
          resolve2(true);
        }).catch((error) => {
          console.log(error);
          resolve2(false);
        });
      }));

      Promise.all(promises).then((data) => {
        if (data && data.length > 0) {
          let done = 0;
          data.forEach((value) => {
            if (value) { done++; }
          });

          if (done === data.length) { resolve(true); } else { resolve(false); }
        }
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public async getAllData(): Promise<{ [key: string]: any }> {
    let allData: { [key: string]: any } = {};
    try {
      // Obtiene todas las claves almacenadas
      const keys = await this.storage.keys();
      
      // Recupera los valores para cada clave y los almacena en allData
      await Promise.all(keys.map(async (key) => {
        const value = await this.storage.get(key);
        allData[key] = value;
      }));

      return allData;
    } catch (error) {
      console.error('Error al obtener los datos del almacenamiento', error);
      throw error; // O manejar el error de alguna otra manera
    }
  }

  public async deleteAllData(): Promise<boolean> {
    try {
      // Obtiene todas las claves almacenadas
      const keys = await this.storage.keys();

      // Borra los datos para cada clave
      await Promise.all(keys.map(async (key) => {
        await this.storage.remove(key);
      }));

      console.log('Todos los datos han sido borrados');
      return true;
    } catch (error) {
      console.error('Error al borrar los datos del almacenamiento', error);
      return false;
    }
  }
  
}
