import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { NotificationType } from 'app/entities/enumerations/notification-type.model';

export interface INotification {
  id: number;
  type?: keyof typeof NotificationType | null;
  title?: string | null;
  message?: string | null;
  read?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  linkUrl?: string | null;
  recipient?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
