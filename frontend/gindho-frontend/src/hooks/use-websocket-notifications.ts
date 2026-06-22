import { useEffect, useCallback, useRef } from 'react';

interface Notification {
  id: string;
  type: 'appointment' | 'lab_result' | 'message' | 'system';
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
}

interface UseWebSocketNotificationsOptions {
  baseUrl?: string;
  onNotification?: (notification: Notification) => void;
}

export function useWebSocketNotifications({
  baseUrl = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8080',
  onNotification,
}: UseWebSocketNotificationsOptions = {}) {
  const wsRef = useRef<WebSocket | null>(null);
  const reconnectTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const connect = useCallback(() => {
    const token = localStorage.getItem('token');
    if (!token) return;

    const wsUrl = `${baseUrl.replace('http', 'ws')}/api/ws?token=${token}`;
    
    wsRef.current = new WebSocket(wsUrl);

    wsRef.current.onopen = () => {
      console.log('WebSocket connected');
    };

    wsRef.current.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        if (data.type === 'notification' && onNotification) {
          onNotification(data.payload);
        }
      } catch (e) {
        console.error('Failed to parse WebSocket message', e);
      }
    };

    wsRef.current.onclose = () => {
      console.log('WebSocket disconnected');
      reconnectTimeoutRef.current = setTimeout(connect, 5000);
    };

    wsRef.current.onerror = (error) => {
      console.error('WebSocket error', error);
    };
  }, [baseUrl, onNotification]);

  const disconnect = useCallback(() => {
    if (wsRef.current) {
      wsRef.current.close();
      wsRef.current = null;
    }
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
    }
  }, []);

  useEffect(() => {
    connect();
    return disconnect;
  }, [connect, disconnect]);

  return { connect, disconnect };
}