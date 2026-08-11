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

import { ownerService } from '../../core/services/owner.service';
import { useAuth } from '../../core/auth/AuthContext';
import { OwnerRequest, OwnerResponse } from '../../core/models/owner.model';
import { Role } from '../../core/models/role.model';

const emptyForm: OwnerRequest = { name: '', email: '', phone: '' };

const OwnerListPage: React.FC = () => {
  const { hasRole } = useAuth();
  const canCreate = hasRole(Role.ADMIN, Role.VET, Role.HAIRDRESSER);
  const canDelete = hasRole(Role.ADMIN);

  const [owners, setOwners] = useState<OwnerResponse[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<OwnerRequest>(emptyForm);

  const load = (event?: CustomEvent<RefresherEventDetail>) => {
    ownerService
      .list()
      .then((data) => setOwners(data))
      .finally(() => event?.detail.complete());
  };

  useEffect(() => {
    load();
  }, []);

  const submit = () => {
    if (!form.name || !form.email) return;
    ownerService.create(form).then(() => {
      setForm(emptyForm);
      setShowForm(false);
      load();
    });
  };

  const remove = (id: number) => {
    ownerService.remove(id).then(() => load());
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Propietarios</IonTitle>
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
                label="Email"
                labelPlacement="stacked"
                type="email"
                value={form.email}
                onIonInput={(e) => setForm({ ...form, email: e.detail.value ?? '' })}
              />
            </IonItem>
            <IonItem>
              <IonInput
                label="Teléfono"
                labelPlacement="stacked"
                value={form.phone}
                onIonInput={(e) => setForm({ ...form, phone: e.detail.value ?? '' })}
              />
            </IonItem>
            <IonButton expand="block" className="ion-margin-top" onClick={submit}>
              Guardar propietario
            </IonButton>
          </IonList>
        )}

        <IonList>
          {owners.map((o) => (
            <IonItemSliding key={o.id}>
              <IonItem>
                <IonLabel>
                  <h2>{o.name}</h2>
                  <p>
                    {o.email} · {o.phone}
                  </p>
                </IonLabel>
              </IonItem>
              {canDelete && (
                <IonItemOptions side="end">
                  <IonItemOption color="danger" onClick={() => remove(o.id)}>
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

export default OwnerListPage;
