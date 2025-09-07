# Shared Todo App

A cross-platform shared todo list application with read and edit links.

## Architecture

This is a monorepo containing:

- `/android` - Native Android app (Kotlin + Jetpack Compose)
- `/web` - Next.js web application
- `/functions` - Firebase Cloud Functions
- `/firestore` - Firestore security rules and indexes

## Backend

- **Firebase Firestore** - Document database for lists and items
- **Firebase Cloud Functions** - Server-side logic for list creation and key rotation
- **Firebase Dynamic Links** - Cross-platform deep linking

## Features

- ✅ Create todo lists with unique shareable links
- ✅ Read-only links for viewing lists
- ✅ Edit links with write keys for modifying lists
- ✅ Rotate write keys to invalidate old edit links
- ✅ Offline support on Android
- ✅ Cross-platform deep linking (Android app ↔ Web fallback)

## Development

See individual directory READMEs for setup instructions.
