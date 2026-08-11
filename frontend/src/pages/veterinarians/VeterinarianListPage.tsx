import React, { useEffect, useState } from 'react';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonPage,
  IonList,
  IonItem,
  IonLabel,
  IonButton,
  IonIcon,
  IonInput,
  IonItemSliding,
  IonItemOptions,
  IonItemOption,
  IonRefresher,
  IonRefresherContent,
  RefresherEventDetail
} from '@ionic/react';
import { addOutline, trashOutline } from 'ionicons/icons';

import { veterinarianService } from '../../core/services/veterinarian.service';
import { useAuth } from '../../core/auth/AuthContext';
import { VeterinarianRequest, VeterinarianResponse } from '../../core/models/veterinarian.model';
import { Role } from '../../core/models/role.model';

const emptyForm: VeterinarianRequest = { name: '', specialty: '' };

const VeterinarianListPage: React.FC = () => {
  const { hasRole } = useAuth();
  // POST -> ADMIN, VET, HAIRDRESSER | DELETE -> exclusivo ADMIN (validado en carpeta Negativos de Postman)
  const canCreate = hasRole(Role.ADMIN, Role.VET, Role.HAIRDRESSER);
  const canDelete = hasRole(Role.ADMIN);

  const [vets, setVets] = useState<VeterinarianResponse[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<VeterinarianRequest>(emptyForm);

  const load = (event?: CustomEvent<RefresherEventDetail>) => {
    veterinarianService
      .list()
      .then((data) => setVets(data))
      .finally(() => event?.detail.complete());
  };

  useEffect(() => {
    load();
  }, []);

  const submit = () => {
    if (!form.name || !form.specialty) return;
    veterinarianService.create(form).then(() => {
      setForm(emptyForm);
      setShowForm(false);
      load();
    });
  };

  const remove = (id: number) => {
    veterinarianService.remove(id).then(() => load());
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Veterinarios</IonTitle>
          {canCreate && (
            <IonButton slot="end" fill="clear" onClick={() => setShowForm(!showForm)}>
              <IonIcon icon={addOutline} />
            </IonButton>
          )}
        </IonToolbar>
      </IonHeader>

      <IonContent>
        <IonRefresher slot="fixed" onIonRefresh={load}>
          <IonRefresherContent />
        </IonRefresher>

        {showForm && (
          <IonList className="ion-padding">
            <IonItem>
              <IonInput
                label="Nombre"
                labelPlacement="stacked"
                value={form.name}
                onIonInput={(e) => setForm({ ...form, name: e.detail.value ?? '' })}
              />
            </IonItem>
            <IonItem>
              <IonInput
                label="Especialidad"
                labelPlacement="stacked"
                value={form.specialty}
                onIonInput={(e) => setForm({ ...form, specialty: e.detail.value ?? '' })}
              />
            </IonItem>
            <IonButton expand="block" className="ion-margin-top" onClick={submit}>
              Guardar veterinario
            </IonButton>
          </IonList>
        )}

        <IonList>
          {vets.map((v) => (
            <IonItemSliding key={v.id}>
              <IonItem>
                <IonLabel>
                  <h2>{v.name}</h2>
                  <p>{v.specialty}</p>
                </IonLabel>
              </IonItem>
              {/* Solo ADMIN ve la opción de borrar: DELETE es exclusivo de ese rol en el backend */}
              {canDelete && (
                <IonItemOptions side="end">
                  <IonItemOption color="danger" onClick={() => remove(v.id)}>
                    <IonIcon icon={trashOutline} />
                  </IonItemOption>
                </IonItemOptions>
              )}
            </IonItemSliding>
          ))}
        </IonList>
      </IonContent>
    </IonPage>
  );
};

export default VeterinarianListPage;
