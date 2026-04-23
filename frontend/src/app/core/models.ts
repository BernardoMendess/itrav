export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest extends AuthRequest {
  name: string;
}

export interface User {
  id: number;
  email: string;
  name: string;
  avatarUrl?: string | null;
  subscriptionTier: 'FREE' | 'PREMIUM';
  preferences?: string | null;
}

export interface AuthPayload {
  token: string;
  type: string;
  expiresIn: number;
  user: User;
}

export interface Trip {
  id?: number;
  title: string;
  destination: string;
  startDate: string;
  endDate: string;
  budget?: number | null;
  groupComposition?: string | null;
  objective: string;
  status?: string | null;
}

export interface Itinerary {
  id: number;
  tripId: number;
  content: string;
  version: number;
  generatedByAI: boolean;
  activities: Activity[];
}

export interface Activity {
  id?: number;
  itineraryId?: number;
  title: string;
  dayNumber?: number;
  description?: string | null;
  startTime?: string | null;
  endTime?: string | null;
  durationMinutes?: number | null;
  latitude?: number | null;
  longitude?: number | null;
  cost?: number | null;
  currency?: string | null;
  provider?: string | null;
  externalId?: string | null;
  orderIndex?: number | null;
}
