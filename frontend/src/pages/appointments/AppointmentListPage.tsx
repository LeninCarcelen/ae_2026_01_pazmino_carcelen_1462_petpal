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
  IonTextarea,
  IonSelect,
  IonSelectOption,
  IonItemSliding,
  IonItemOptions,
  IonItemOption,
  IonBadge,
  IonRefresher,
  IonRefresherContent,
  RefresherEventDetail
} from '@ionic/react';
import { addOutline, trashOutline, checkmarkDoneOutline } from 'ionicons/icons';

import { appointmentService } from '../../core/services/appointment.service';
import { petService } from '../../core/services/pet.service';
import { veterinarianService } from '../../core/services/veterinarian.service';
import { useAuth } from '../../core/auth/AuthContext';
import { AppointmentRequest, AppointmentResponse } from '../../core/models/appointment.model';
import { PetResponse } from '../../core/models/pet.model';
import { VeterinarianResponse } from '../../core/models/veterinarian.model';
import { Role } from '../../core/models/role.model';

const emptyForm: AppointmentRequest = { date: '', reason: '', petId: 0, veterinarianIds: [] };

const formatDate = (iso: string): string => {
  if (!iso) return '';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  return d.toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });
};

const statusColor = (status: string): string => {
  switch (status) {
    case 'COMPLETED':
      return 'success';
    case 'CANCELLED':
      return 'danger';
    default:
      return 'warning';
  }
};

const AppointmentListPage: React.FC = () => {
  const { hasRole } = useAuth();
  // POST/PATCH -> ADMIN, VET, HAIRDRESSER | DELETE -> exclusivo ADMIN
  const canCreate = hasRole(Role.ADMIN, Role.VET, Role.HAIRDRESSER);
  const canUpdateStatus = hasRole(Role.ADMIN, Role.VET, Role.HAIRDRESSER);
  const canDelete = hasRole(Role.ADMIN);

  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [pets, setPets] = useState<PetResponse[]>([]);
  const [vets, setVets] = useState<VeterinarianResponse[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<AppointmentRequest>(emptyForm);

  const load = (event?: CustomEvent<RefresherEventDetail>) => {
    appointmentService
      .list()
      .then((data) => setAppointments(data))
      .finally(() => event?.detail.complete());
  };

  useEffect(() => {
    load();
    petService.list().then(setPets);
    veterinarianService.list().then(setVets);
  }, []);

  const openCreate = () => {
    setForm({ ...emptyForm, petId: pets[0]?.id ?? 0 });
    setShowForm(true);
  };

  const submit = () => {
    if (!form.date || !form.reason || !form.petId || form.veterinarianIds.length === 0) return;
    appointmentService.create(form).then(() => {
      setShowForm(false);
      load();
    });
  };

  const updateStatus = (appointment: AppointmentResponse, status: string) => {
    appointmentService.updateStatus(appointment.id, status).then(() => load());
  };

  const remove = (id: number) => {
    appointmentService.remove(id).then(() => load());
  };

  const vetNames = (appointment: AppointmentResponse): string =>
    appointment.veterinarians.map((v) => v.name).join(', ');

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Citas</IonTitle>
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
                label="Fecha y hora"
                labelPlacement="stacked"
                type="datetime-local"
                value={form.date}
                onIonInput={(e) => setForm({ ...form, date: e.detail.value ?? '' })}
              />
            </IonItem>
            <IonItem>
              <IonTextarea
                label="Motivo"
                labelPlacement="stacked"
                value={form.reason}
                onIonInput={(e) => setForm({ ...form, reason: e.detail.value ?? '' })}
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
            <IonItem>
              <IonSelect
                label="Veterinarios"
                labelPlacement="stacked"
                multiple
                value={form.veterinarianIds}
                onIonChange={(e) => setForm({ ...form, veterinarianIds: e.detail.value })}
              >
                {vets.map((v) => (
                  <IonSelectOption key={v.id} value={v.id}>
                    {v.name}
                  </IonSelectOption>
                ))}
              </IonSelect>
            </IonItem>
            <IonButton expand="block" className="ion-margin-top" onClick={submit}>
              Crear cita
            </IonButton>
          </IonList>
        )}

        <IonList>
          {appointments.map((a) => (
            <IonItemSliding key={a.id}>
              <IonItem>
                <IonLabel>
                  <h2>
                    {a.petName} — {formatDate(a.date)}
                  </h2>
                  <p>{a.reason}</p>
                  <p>Vets: {vetNames(a)}</p>
                </IonLabel>
                <IonBadge slot="end" color={statusColor(a.status)}>
                  {a.status}
                </IonBadge>
              </IonItem>
              <IonItemOptions side="end">
                {canUpdateStatus && a.status !== 'COMPLETED' && (
                  <IonItemOption color="success" onClick={() => updateStatus(a, 'COMPLETED')}>
                    <IonIcon icon={checkmarkDoneOutline} />
                  </IonItemOption>
                )}
                {canDelete && (
                  <IonItemOption color="danger" onClick={() => remove(a.id)}>
                    <IonIcon icon={trashOutline} />
                  </IonItemOption>
                )}
              </IonItemOptions>
            </IonItemSliding>
          ))}
        </IonList>
      </IonContent>
    </IonPage>
  );
};

export default AppointmentListPage;
