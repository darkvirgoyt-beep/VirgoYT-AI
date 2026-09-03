'use client';

import { useRef } from 'react';
import { useFrame } from '@react-three/fiber';
import * as THREE from 'three';

export function HolographicCore() {
  const outer = useRef<THREE.Mesh>(null);
  const inner = useRef<THREE.Mesh>(null);
  const ring1 = useRef<THREE.Mesh>(null);
  const ring2 = useRef<THREE.Mesh>(null);

  useFrame(({ clock }) => {
    const t = clock.getElapsedTime();
    if (outer.current) {
      outer.current.rotation.x = t * 0.2;
      outer.current.rotation.y = t * 0.3;
    }
    if (inner.current) {
      inner.current.rotation.x = -t * 0.35;
      inner.current.rotation.y = -t * 0.25;
      inner.current.position.y = Math.sin(t) * 0.3;
    }
    if (ring1.current) {
      ring1.current.rotation.z = t * 0.5;
      ring1.current.rotation.x = Math.PI / 2 + Math.sin(t * 0.3) * 0.2;
    }
    if (ring2.current) {
      ring2.current.rotation.z = -t * 0.4;
      ring2.current.rotation.x = Math.PI / 2 + Math.cos(t * 0.25) * 0.25;
    }
  });

  return (
    <group position={[0, 0, 0]}>
      <mesh ref={outer}>
        <icosahedronGeometry args={[2.5, 1]} />
        <meshStandardMaterial
          color="#3375ff"
          wireframe
          transparent
          opacity={0.5}
          emissive="#1d53f5"
          emissiveIntensity={0.4}
        />
      </mesh>

      <mesh ref={inner}>
        <icosahedronGeometry args={[1.4, 0]} />
        <meshStandardMaterial
          color="#00d4ff"
          wireframe
          transparent
          opacity={0.8}
          emissive="#00d4ff"
          emissiveIntensity={0.9}
        />
      </mesh>

      <mesh ref={ring1}>
        <torusGeometry args={[4, 0.02, 8, 8]} />
        <meshBasicMaterial color="#b967ff" transparent opacity={0.7} />
      </mesh>

      <mesh ref={ring2}>
        <torusGeometry args={[5, 0.015, 8, 8]} />
        <meshBasicMaterial color="#00ff9c" transparent opacity={0.5} />
      </mesh>

      <pointLight color="#3375ff" intensity={2.5} distance={20} />
    </group>
  );
}
