'use client';

import { useState, useEffect } from 'react';
import { useParams, useSearchParams } from 'next/navigation';
import { collection, query, orderBy, onSnapshot, addDoc, updateDoc, doc, serverTimestamp } from 'firebase/firestore';
import { db } from '../../../lib/firebase';
import { rotateKey } from '../../../lib/api';

interface TodoItem {
  id: string;
  text: string;
  done: boolean;
  position: number;
  createdAt: Date;
}

export default function TodoList() {
  const params = useParams();
  const searchParams = useSearchParams();
  const shortId = params.shortId as string;
  const writeKey = searchParams.get('k');

  const [items, setItems] = useState<TodoItem[]>([]);
  const [newItemText, setNewItemText] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isRotating, setIsRotating] = useState(false);

  const isReadOnly = !writeKey;

  useEffect(() => {
    const q = query(
      collection(db, 'lists', shortId, 'items'),
      orderBy('position', 'asc')
    );

    const unsubscribe = onSnapshot(q, (querySnapshot) => {
      const itemsData: TodoItem[] = [];
      querySnapshot.forEach((doc) => {
        const data = doc.data();
        if (data.text && data.text.trim()) { // Filter out deleted items
          itemsData.push({
            id: doc.id,
            text: data.text,
            done: data.done || false,
            position: data.position || 0,
            createdAt: data.createdAt?.toDate() || new Date(),
          });
        }
      });
      setItems(itemsData);
      setIsLoading(false);
    });

    return () => unsubscribe();
  }, [shortId]);

  const addItem = async () => {
    if (!newItemText.trim() || isReadOnly) return;

    try {
      await addDoc(collection(db, 'lists', shortId, 'items'), {
        text: newItemText.trim(),
        done: false,
        position: items.length,
        createdAt: serverTimestamp(),
        k: writeKey, // Include write key for authorization
      });
      setNewItemText('');
    } catch (error) {
      console.error('Error adding item:', error);
      alert('Failed to add item');
    }
  };

  const toggleItem = async (itemId: string, done: boolean) => {
    if (isReadOnly) return;

    try {
      await updateDoc(doc(db, 'lists', shortId, 'items', itemId), {
        done,
        k: writeKey,
      });
    } catch (error) {
      console.error('Error updating item:', error);
      alert('Failed to update item');
    }
  };

  const deleteItem = async (itemId: string) => {
    if (isReadOnly) return;

    try {
      await updateDoc(doc(db, 'lists', shortId, 'items', itemId), {
        text: '',
        done: true,
        k: writeKey,
      });
    } catch (error) {
      console.error('Error deleting item:', error);
      alert('Failed to delete item');
    }
  };

  const handleRotateKey = async () => {
    setIsRotating(true);
    try {
      const response = await rotateKey(shortId);
      // Update the URL with the new write key
      window.history.replaceState({}, '', `/l/${shortId}?k=${response.writeKey}`);
      alert('Write key rotated successfully!');
    } catch (error) {
      console.error('Error rotating key:', error);
      alert('Failed to rotate key');
    } finally {
      setIsRotating(false);
    }
  };

  const shareReadLink = () => {
    const link = `${window.location.origin}/l/${shortId}`;
    navigator.clipboard.writeText(link);
    alert('Read link copied to clipboard!');
  };

  const shareEditLink = () => {
    if (!writeKey) return;
    const link = `${window.location.origin}/l/${shortId}?k=${writeKey}`;
    navigator.clipboard.writeText(link);
    alert('Edit link copied to clipboard!');
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-64">
        <div className="text-gray-500">Loading...</div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">
          List {shortId}
        </h1>

        <div className="flex gap-2">
          <button
            onClick={shareReadLink}
            className="btn-secondary text-sm"
          >
            Share Read Link
          </button>

          {!isReadOnly && (
            <>
              <button
                onClick={shareEditLink}
                className="btn-secondary text-sm"
              >
                Share Edit Link
              </button>

              <button
                onClick={handleRotateKey}
                disabled={isRotating}
                className="btn-secondary text-sm disabled:opacity-50"
              >
                {isRotating ? 'Rotating...' : 'Reset Key'}
              </button>
            </>
          )}
        </div>
      </div>

      {isReadOnly && (
        <div className="bg-yellow-100 border border-yellow-400 text-yellow-700 px-4 py-3 rounded mb-6">
          Read-only mode
        </div>
      )}

      {/* Add new item */}
      {!isReadOnly && (
        <div className="flex gap-2 mb-6">
          <input
            type="text"
            value={newItemText}
            onChange={(e) => setNewItemText(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && addItem()}
            placeholder="Add new item..."
            className="input flex-1"
          />
          <button
            onClick={addItem}
            disabled={!newItemText.trim()}
            className="btn whitespace-nowrap disabled:opacity-50"
          >
            Add
          </button>
        </div>
      )}

      {/* Items list */}
      <div className="space-y-2">
        {items.map((item) => (
          <div
            key={item.id}
            className={`flex items-center gap-3 p-3 border rounded-lg ${
              item.done ? 'bg-gray-50' : 'bg-white'
            }`}
          >
            <input
              type="checkbox"
              checked={item.done}
              onChange={(e) => toggleItem(item.id, e.target.checked)}
              disabled={isReadOnly}
              className="w-5 h-5"
            />

            <span
              className={`flex-1 ${
                item.done ? 'line-through text-gray-500' : 'text-gray-900'
              }`}
            >
              {item.text}
            </span>

            {!isReadOnly && (
              <button
                onClick={() => deleteItem(item.id)}
                className="text-red-500 hover:text-red-700 px-2 py-1 text-sm"
              >
                Delete
              </button>
            )}
          </div>
        ))}

        {items.length === 0 && (
          <div className="text-center py-8 text-gray-500">
            No items yet. {isReadOnly ? 'Ask for edit permission to add items.' : 'Add your first item above.'}
          </div>
        )}
      </div>
    </div>
  );
}
