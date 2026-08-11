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
import { addOutline, trashOutline, createOutline } from 'ionicons/icons';

import { petService } from '../../core/services/pet.service';
import { ownerService } from '../../core/services/owner.service';
import { useAuth } from '../../core/auth/AuthContext';
import { PetRequest, PetResponse } from '../../core/models/pet.model';
import { OwnerResponse } from '../../core/models/owner.model';
import { Role } from '../../core/models/role.model';

const emptyForm: PetRequest = { name: '', species: '', breed: '', birthDate: null, ownerId: 0 };

const PetListPage: React.FC = () => {
  const { hasRole } = useAuth();
  const canWrite = hasRole(Role.ADMIN, Role.VET, Role.HAIRDRESSER);
  const canDelete = hasRole(Role.ADMIN);

  const [pets, setPets] = useState<PetResponse[]>([]);
  const [owners, setOwners] = useState<OwnerResponse[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState<PetRequest>(emptyForm);

  const load = (event?: CustomEvent<RefresherEventDetail>) => {
    petService
      .list()
      .then((data) => setPets(data))
      .finally(() => event?.detail.complete());
  };

  useEffect(() => {
    load();
    ownerService.list().then(setOwners);
  }, []);

  const openCreate = () => {
    setEditingId(null);
    setForm({ ...emptyForm, ownerId: owners[0]?.id ?? 0 });
    setShowForm(true);
  };

  const openEdit = (pet: PetResponse) => {
    setEditingId(pet.id);
    setForm({
      name: pet.name,
      species: pet.species,
      breed: pet.breed,
      birthDate: pet.birthDate,
      ownerId: pet.ownerId
    });
    setShowForm(true);
  };

  const submit = () => {
    if (!form.name || !form.ownerId) return;
    const obs = editingId ? petService.update(editingId, form) : petService.create(form);
    obs.then(() => {
      setShowForm(false);
      load();
    });
  };

  const remove = (id: number) => {
    petService.remove(id).then(() => load());
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Mascotas</IonTitle>
          {canWrite && (
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
                label="Especie"
                labelPlacement="stacked"
                value={form.species}
                onIonInput={(e) => setForm({ ...form, species: e.detail.value ?? '' })}
              />
            </IonItem>
            <IonItem>
              <IonInput
                label="Raza"
                labelPlacement="stacked"
                value={form.breed}
                onIonInput={(e) => setForm({ ...form, breed: e.detail.value ?? '' })}
              />
            </IonItem>
            <IonItem>
              <IonInput
                label="Fecha de nacimiento"
                labelPlacement="stacked"
                type="date"
                value={form.birthDate ?? ''}
                onIonInput={(e) => setForm({ ...form, birthDate: e.detail.value || null })}
              />
            </IonItem>
            <IonItem>
              <IonSelect
                label="Propietario"
                labelPlacement="stacked"
                value={form.ownerId}
                onIonChange={(e) => setForm({ ...form, ownerId: e.detail.value })}
              >
                {owners.map((o) => (
                  <IonSelectOption key={o.id} value={o.id}>
                    {o.name}
                  </IonSelectOption>
                ))}
              </IonSelect>
            </IonItem>
            <IonButton expand="block" className="ion-margin-top" onClick={submit}>
              Guardar mascota
            </IonButton>
          </IonList>
        )}

        <IonList>
          {pets.map((p) => (
            <IonItemSliding key={p.id}>
              <IonItem>
                <IonLabel>
                  <h2>
                    {p.name} ({p.species})
                  </h2>
                  <p>
                    {p.breed} · Dueño: {p.ownerName}
                  </p>
                </IonLabel>
                {canWrite && (
                  <IonButton slot="end" fill="clear" onClick={() => openEdit(p)}>
                    <IonIcon icon={createOutline} />
                  </IonButton>
                )}
              </IonItem>
              {canDelete && (
                <IonItemOptions side="end">
                  <IonItemOption color="danger" onClick={() => remove(p.id)}>
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

export default PetListPage;
