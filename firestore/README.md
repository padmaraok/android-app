# Firestore Configuration

This directory contains Firestore security rules and indexes for the Shared Todo App.

## Files

- `firestore.rules` - Security rules for Firestore collections
- `firestore.indexes.json` - Firestore indexes for optimal query performance

## Data Model

### Lists Collection

```
/lists/{shortId}
├── title?: string
├── createdAt: timestamp
└── publicRead: true
```

### Items Subcollection

```
/lists/{shortId}/items/{itemId}
├── text: string
├── done: boolean
├── position: number
└── createdAt: timestamp
```

### Keys Collection

```
/keys/{shortId}
└── writeKey: string (UUID)
```

## Security Rules

- Lists are publicly readable if `publicRead: true`
- Items are publicly readable but require write key for mutations
- Write keys are never readable by clients (server-only)
- List creation and key rotation handled via Cloud Functions

## Deployment

```bash
firebase deploy --only firestore:rules
```
