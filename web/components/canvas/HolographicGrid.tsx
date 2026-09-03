'use client';

import { useMemo, useRef } from 'react';
import { useFrame } from '@react-three/fiber';
import * as THREE from 'three';

export function HolographicGrid() {
  const gridRef = useRef<THREE.GridHelper>(null);
  const grid2Ref = useRef<THREE.GridHelper>(null);

  useFrame(({ clock }) => {
    const t = clock.getElapsedTime();
    if (gridRef.current) {
      gridRef.current.position.z = (t * 0.3) % 2;
    }
  });

  const material = useMemo(
    () =>
      new THREE.LineBasicMaterial({
        color: new THREE.Color('#3375ff'),
        transparent: true,
        opacity: 0.35,
        blending: THREE.AdditiveBlending,
      }),
    []
  );

  return (
    <group>
      <gridHelper
        ref={gridRef}
        args={[120, 80, '#1d53f5', '#142256']}
        position={[0, -8, 0]}
      />
      <gridHelper
        ref={grid2Ref}
        args={[160, 100, '#b967ff', '#291a47']}
        position={[0, -8, 0]}
        rotation={[0, Math.PI / 2, 0]}
      />
      <mesh rotation={[-Math.PI / 2, 0, 0]} position={[0, -8.05, 0]}>
        <planeGeometry args={[160, 160]} />
        <meshBasicMaterial
          color="#05060f"
          transparent
          opacity={0.9}
        />
      </mesh>
    </group>
  );
}
