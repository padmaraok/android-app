'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { createList } from '../lib/api';

export default function Home() {
  const [isLoading, setIsLoading] = useState(false);
  const [listCode, setListCode] = useState('');
  const router = useRouter();

  const handleCreateList = async () => {
    setIsLoading(true);
    try {
      const response = await createList();
      router.push(`/l/${response.shortId}?k=${response.writeKey}`);
    } catch (error) {
      console.error('Failed to create list:', error);
      alert('Failed to create list. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleOpenList = () => {
    if (listCode.trim()) {
      router.push(`/l/${listCode.trim().toUpperCase()}`);
    }
  };

  return (
    <div className="max-w-md mx-auto">
      <h1 className="text-3xl font-bold text-center mb-8 text-gray-900">
        Shared Todo
      </h1>

      <div className="space-y-6">
        {/* Create New List */}
        <div className="text-center">
          <button
            onClick={handleCreateList}
            disabled={isLoading}
            className="btn w-full py-3 text-lg disabled:opacity-50"
          >
            {isLoading ? 'Creating...' : 'New List'}
          </button>
        </div>

        {/* Divider */}
        <div className="relative">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-gray-300"></div>
          </div>
          <div className="relative flex justify-center text-sm">
            <span className="px-2 bg-gray-50 text-gray-500">or</span>
          </div>
        </div>

        {/* Open Existing List */}
        <div className="space-y-4">
          <input
            type="text"
            placeholder="Enter list code (e.g., ABCD1234)"
            value={listCode}
            onChange={(e) => setListCode(e.target.value.toUpperCase())}
            className="input"
          />
          <button
            onClick={handleOpenList}
            disabled={!listCode.trim()}
            className="btn-secondary w-full disabled:opacity-50"
          >
            Open List
          </button>
        </div>
      </div>
    </div>
  );
}
