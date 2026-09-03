'use client';

import { useRef } from 'react';
import { useFrame } from '@react-three/fiber';
import * as THREE from 'three';

export function ConnectionStatus({ active }: { active?: boolean }) {
  const mesh = useRef<THREE.TorusKnotGeometry>(null);

  const color = active ? '#00ff9c' : '#ff5c7a';

  return (
    <group position={[6, 4, -4]}>
      <mesh>
        <torusKnotGeometry args={[0.4, 0.12, 64, 8]} />
        <meshStandardMaterial
          color={color}
          emissive={color}
          emissiveIntensity={0.6}
          transparent
          opacity={0.8}
        />
      </mesh>
      <mesh position={[0, 1.2, 0]}>
        <planeGeometry args={[2, 0.5]} />
        <meshBasicMaterial color={color} transparent opacity={0.4} />
      </mesh>
    </group>
  );
}
