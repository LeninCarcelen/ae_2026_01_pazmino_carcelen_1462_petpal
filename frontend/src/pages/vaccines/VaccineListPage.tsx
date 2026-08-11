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
  IonSelect,
  IonSelectOption,
  IonItemSliding,
  IonItemOptions,
  IonItemOption,
  IonRefresher,
  IonRefresherContent,
  RefresherEventDetail
} from '@ionic/react';
import { addOutline, trashOutline } from 'ionicons/icons';

import { vaccineService } from '../../core/services/vaccine.service';
import { petService } from '../../core/services/pet.service';
import { useAuth } from '../../core/auth/AuthContext';
import { VaccineRequest, VaccineResponse } from '../../core/models/vaccine.model';
import { PetResponse } from '../../core/models/pet.model';
import { Role } from '../../core/models/role.model';

const emptyForm: VaccineRequest = { name: '', dateApplied: '', nextDueDate: null, petId: 0 };

const VaccineListPage: React.FC = () => {
  const { hasRole } = useAuth();
  const canCreate = hasRole(Role.ADMIN, Role.VET, Role.HAIRDRESSER);
  const canDelete = hasRole(Role.ADMIN);

  const [vaccines, setVaccines] = useState<VaccineResponse[]>([]);
  const [pets, setPets] = useState<PetResponse[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<VaccineRequest>(emptyForm);

  const load = (event?: CustomEvent<RefresherEventDetail>) => {
    vaccineService
      .list()
      .then((data) => setVaccines(data))
      .finally(() => event?.detail.complete());
  };

  useEffect(() => {
    load();
    petService.list().then(setPets);
  }, []);

  const openCreate = () => {
    setForm({ ...emptyForm, petId: pets[0]?.id ?? 0 });
    setShowForm(true);
  };

  const submit = () => {
    if (!form.name || !form.dateApplied || !form.petId) return;
    vaccineService.create(form).then(() => {
      setShowForm(false);
      load();
    });
  };

  const remove = (id: number) => {
    vaccineService.remove(id).then(() => load());
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Vacunas</IonTitle>
          {canCreate && (
            <IonButton slot="end" fill="clear" onClick={openCreate}>
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
                label="Fecha aplicada"
                labelPlacement="stacked"
                type="date"
                value={form.dateApplied}
                onIonInput={(e) => setForm({ ...form, dateApplied: e.detail.value ?? '' })}
              />
            </IonItem>
            <IonItem>
              <IonInput
                label="Próxima dosis"
                labelPlacement="stacked"
                type="date"
                value={form.nextDueDate ?? ''}
                onIonInput={(e) => setForm({ ...form, nextDueDate: e.detail.value || null })}
              />
            </IonItem>
            <IonItem>
              <IonSelect
                label="Mascota"
                labelPlacement="stacked"
                value={form.petId}
                onIonChange={(e) => setForm({ ...form, petId: e.detail.value })}
              >
                {pets.map((p) => (
                  <IonSelectOption key={p.id} value={p.id}>
                    {p.name}
                  </IonSelectOption>
                ))}
              </IonSelect>
            </IonItem>
            <IonButton expand="block" className="ion-margin-top" onClick={submit}>
              Registrar vacuna
            </IonButton>
          </IonList>
        )}

        <IonList>
          {vaccines.map((v) => (
            <IonItemSliding key={v.id}>
              <IonItem>
                <IonLabel>
                  <h2>{v.name}</h2>
                  <p>
                    {v.petName} · Aplicada: {v.dateApplied} · Próxima: {v.nextDueDate || '—'}
                  </p>
                </IonLabel>
              </IonItem>
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

export default VaccineListPage;
