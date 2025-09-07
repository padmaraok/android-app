# Shared Todo Web App

Next.js web application for the Shared Todo list system.

## Features

- ✅ Create new todo lists
- ✅ Open existing lists by code
- ✅ Real-time synchronization with Firestore
- ✅ Read-only and edit modes
- ✅ Share read and edit links
- ✅ Rotate write keys
- ✅ Responsive design

## Setup

1. Install dependencies:
```bash
npm install
```

2. Configure environment variables in `.env.local`:
```bash
NEXT_PUBLIC_FIREBASE_API_KEY=your_api_key
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
NEXT_PUBLIC_FIREBASE_PROJECT_ID=your_project_id
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
NEXT_PUBLIC_FIREBASE_APP_ID=your_app_id
NEXT_PUBLIC_FUNCTIONS_BASE_URL=https://us-central1-your_project.cloudfunctions.net
```

3. Run the development server:
```bash
npm run dev
```

## Deployment

Deploy to Vercel or Firebase Hosting:

```bash
npm run build
npm run start
```

## Pages

- `/` - Home page with create/open list functionality
- `/l/[shortId]` - Todo list view (read-only)
- `/l/[shortId]?k=writeKey` - Todo list view (edit mode)

## API Integration

The app integrates with Firebase Cloud Functions for:
- Creating new lists
- Rotating write keys

All Firestore operations are handled client-side with proper security rules.
