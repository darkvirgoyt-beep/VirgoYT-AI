'use client';

import { useRef } from 'react';
import { useFrame } from '@react-three/fiber';
import * as THREE from 'three';

// Concentric wireframe orbit rings that gently rotate on different axes to
// give the background a sense of depth and motion around the core.
export function OrbitRings({ rings = 3 }: { rings?: number }) {
  const refs = useRef<(THREE.Mesh | null)[]>([]);

  const palette = ['#3375ff', '#b967ff', '#00d4ff'];

  useFrame(({ clock }) => {
    const t = clock.getElapsedTime();
    refs.current.forEach((m, i) => {
      if (!m) return;
      m.rotation.z = t * (0.1 + i * 0.12);
      m.rotation.x = Math.PI / 2 + Math.sin(t * 0.15 + i) * 0.25;
      m.rotation.y = t * (0.06 + i * 0.08);
      m.position.y = Math.sin(t * 0.3 + i) * 0.4;
    });
  });

  return (
    <group>
      {Array.from({ length: rings }, (_, i) => {
        const j = Math.min(i, palette.length - 1);
        return (
          <mesh
            key={i}
            ref={(n) => {
              refs.current[i] = n;
            }}
            scale={1 + i * 0.25}
          >
            <torusGeometry args={[5.5 + i * 1.4, 0.012, 8, 64]} />
            <meshBasicMaterial color={palette[j]} transparent opacity={0.5 - i * 0.1} />
          </mesh>
        );
      })}
    </group>
  );
}