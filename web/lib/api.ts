const FUNCTIONS_BASE_URL = process.env.NEXT_PUBLIC_FUNCTIONS_BASE_URL || '';

export interface CreateListResponse {
  shortId: string;
  writeKey: string;
  readLink: string;
  editLink: string;
}

export interface RotateKeyResponse {
  writeKey: string;
  editLink: string;
}

export async function createList(): Promise<CreateListResponse> {
  const response = await fetch(`${FUNCTIONS_BASE_URL}/createList`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to create list: ${response.statusText}`);
  }

  return response.json();
}

export async function rotateKey(shortId: string): Promise<RotateKeyResponse> {
  const response = await fetch(`${FUNCTIONS_BASE_URL}/rotateKey?shortId=${shortId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to rotate key: ${response.statusText}`);
  }

  return response.json();
}
