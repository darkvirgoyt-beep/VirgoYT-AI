'use client';

import { useEffect, useRef, useState } from 'react';
import { io, Socket } from 'socket.io-client';
import { API_URL } from '@/lib/api';

export function useTerminalSocket(sessionId: string | null) {
  const socketRef = useRef<Socket | null>(null);
  const [connected, setConnected] = useState(false);
  const onDataRef = useRef<(data: string) => void>(() => {});
  const onReadyRef = useRef<(sid: string) => void>(() => {});

  const connect = (sid: string) => {
    if (!socketRef.current || !socketRef.current.connected) {
      const socket: Socket = io(API_URL, {
        transports: ['websocket'],
        query: { sessionId: sid },
        reconnection: true,
        reconnectionDelay: 1000,
      });
      socketRef.current = socket;

      socket.on('connect', () => {
        setConnected(true);
        socket.emit('terminal:start', { sessionId: sid });
      });

      socket.on('disconnect', () => setConnected(false));

      socket.on('terminal:data', (data: string) => {
        onDataRef.current(data);
      });

      socket.on('terminal:ready', (data: { sessionId: string }) => {
        onReadyRef.current(data.sessionId);
      });
    } else {
      socketRef.current.emit('terminal:start', { sessionId: sid });
    }
  };

  const write = (data: string) => {
    socketRef.current?.emit('terminal:input', { data });
  };

  const resize = (cols: number, rows: number) => {
    socketRef.current?.emit('terminal:resize', { cols, rows });
  };

  const stop = () => {
    socketRef.current?.emit('terminal:stop', {});
  };

  const disconnect = () => {
    socketRef.current?.disconnect();
    socketRef.current = null;
    setConnected(false);
  };

  useEffect(() => {
    return () => {
      socketRef.current?.disconnect();
    };
  }, []);

  return { connected, connect, write, resize, stop, disconnect, onData: (cb: (d: string) => void) => (onDataRef.current = cb), onReady: (cb: (s: string) => void) => (onReadyRef.current = cb) };
}
