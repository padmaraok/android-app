# Firebase Cloud Functions

This directory contains the Firebase Cloud Functions for the Shared Todo App.

## Functions

### `createList`

Creates a new todo list with a unique short ID and write key.

**Endpoint:** `POST /createList`

**Response:**

```json
{
  "shortId": "ABCD1234",
  "writeKey": "uuid-string",
  "readLink": "https://todo.yourdomain.com/l/ABCD1234",
  "editLink": "https://todo.yourdomain.com/l/ABCD1234?k=uuid-string"
}
```

### `rotateKey`

Rotates the write key for an existing list, invalidating old edit links.

**Endpoint:** `POST /rotateKey?shortId=ABCD1234`

**Response:**

```json
{
  "writeKey": "new-uuid-string",
  "editLink": "https://todo.yourdomain.com/l/ABCD1234?k=new-uuid-string"
}
```

## Setup

1. Install dependencies:

```bash
npm install
```

2. Build the functions:

```bash
npm run build
```

3. Deploy to Firebase:

```bash
npm run deploy
```

## Development

Run the emulator locally:

```bash
npm run serve
```

## CORS

Functions include CORS headers to allow cross-origin requests from web applications.
