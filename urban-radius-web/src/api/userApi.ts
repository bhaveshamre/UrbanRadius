import { UserProfile } from '../types/user';

export async function getUserProfile(providerId: string): Promise<UserProfile> {
  const response = await fetch(`/api/users/${providerId}`);

  if (!response.ok) {
    throw new Error(`Failed to load provider ${providerId}`);
  }

  return response.json() as Promise<UserProfile>;
}

export async function fetchProviderProfiles(
  providerIds: string[],
): Promise<Record<string, UserProfile>> {
  const uniqueIds = [...new Set(providerIds)];
  const pairs = await Promise.all(
    uniqueIds.map(async (id) => {
      try {
        const profile = await getUserProfile(id);
        return [id, profile] as const;
      } catch {
        return null;
      }
    }),
  );

  return Object.fromEntries(pairs.filter((entry): entry is [string, UserProfile] => entry !== null));
}

export async function fetchMe(token: string): Promise<UserProfile> {
  const response = await fetch('/api/users/me', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    throw new Error('Session expired — please log in again');
  }

  return response.json() as Promise<UserProfile>;
}
