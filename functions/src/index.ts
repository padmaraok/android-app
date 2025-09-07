import * as admin from "firebase-admin";
import * as functions from "firebase-functions/v2/https";
import { v4 as uuid } from "uuid";
import { customAlphabet } from "nanoid";

admin.initializeApp();

const nanoid = customAlphabet("ABCDEFGHJKLMNPQRSTUVWXYZ23456789", 8);

export const createList = functions.onRequest(async (req, res) => {
  // Enable CORS
  res.set("Access-Control-Allow-Origin", "*");
  res.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  res.set("Access-Control-Allow-Headers", "Content-Type");

  if (req.method === "OPTIONS") {
    res.status(204).send("");
    return;
  }

  if (req.method !== "POST") {
    res.status(405).json({ error: "Method not allowed" });
    return;
  }

  try {
    const shortId = nanoid();
    const writeKey = uuid();
    const now = admin.firestore.FieldValue.serverTimestamp();

    // Create the list document
    await admin.firestore().doc(`lists/${shortId}`).set({
      publicRead: true,
      createdAt: now,
    });

    // Create the write key document
    await admin.firestore().doc(`keys/${shortId}`).set({
      writeKey,
    });

    res.json({
      shortId,
      writeKey,
      readLink: `https://todo.yourdomain.com/l/${shortId}`,
      editLink: `https://todo.yourdomain.com/l/${shortId}?k=${writeKey}`,
    });
  } catch (error) {
    console.error("Error creating list:", error);
    res.status(500).json({ error: "Internal server error" });
  }
});

export const rotateKey = functions.onRequest(async (req, res) => {
  // Enable CORS
  res.set("Access-Control-Allow-Origin", "*");
  res.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  res.set("Access-Control-Allow-Headers", "Content-Type");

  if (req.method === "OPTIONS") {
    res.status(204).send("");
    return;
  }

  if (req.method !== "POST") {
    res.status(405).json({ error: "Method not allowed" });
    return;
  }

  try {
    const { shortId } = req.query as { shortId: string };

    if (!shortId) {
      res.status(400).json({ error: "shortId required" });
      return;
    }

    // Check if the list exists
    const listDoc = await admin.firestore().doc(`lists/${shortId}`).get();
    if (!listDoc.exists) {
      res.status(404).json({ error: "List not found" });
      return;
    }

    const writeKey = uuid();

    // Update the write key
    await admin.firestore().doc(`keys/${shortId}`).set({
      writeKey,
    });

    res.json({
      writeKey,
      editLink: `https://todo.yourdomain.com/l/${shortId}?k=${writeKey}`,
    });
  } catch (error) {
    console.error("Error rotating key:", error);
    res.status(500).json({ error: "Internal server error" });
  }
});
