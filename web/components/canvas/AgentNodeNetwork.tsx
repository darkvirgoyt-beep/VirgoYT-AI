'use client';

import { useMemo, useRef } from 'react';
import { useFrame } from '@react-three/fiber';
import * as THREE from 'three';

// Agent-node constellation: a tilted ring of emissive agent spheres orbiting the
// core, pulsing in a travelling wave so it reads as a live workforce. A faint
// channel-ring under the nodes hints at the connecting mesh.
export function AgentNodeNetwork({ nodes = 12, radius = 7.5 }: { nodes?: number; radius?: number }) {
  const nodeRefs = useRef<(THREE.Mesh | null)[]>([]);

  const positions = useMemo(() => {
    const arr: [number, number, number][] = [];
    const tilt = [0.35, 0, 0.15] as [number, number, number]; // slight roll for depth
    for (let i = 0; i < nodes; i++) {
      const a = (i / nodes) * Math.PI * 2;
      const x = Math.cos(a) * radius;
      const y = Math.sin(a * 2) * 1.3;
      const z = Math.sin(a) * radius;
      // apply a light tilt so the ring reads in perspective
      const cy = y * Math.cos(tilt[0]) - z * Math.sin(tilt[0]);
      const cz = y * Math.sin(tilt[0]) + z * Math.cos(tilt[0]);
      arr.push([x, cy, cz]);
    }
    return arr;
  }, [nodes, radius]);

  const palette = ['#00d4ff', '#b967ff', '#00ff9c'];

  useFrame(({ clock }) => {
    const t = clock.getElapsedTime();
    nodeRefs.current.forEach((n, i) => {
      if (n) {
        n.scale.setScalar(0.8 + 0.4 * Math.sin(t * 1.4 + i * 0.6));
        n.position.y = positions[i][1] + Math.sin(t * 0.8 + i * 0.7) * 0.25;
        n.rotation.y += 0.01;
      }
    });
  });

  return (
    <group position={[0, 1.5, 0]}>
      {/* faint channel ring hinting at the mesh */}
      <mesh rotation={[-Math.PI / 2, 0, 0]} position={[0, -0.6, 0]}>
        <torusGeometry args={[radius * 0.98, 0.008, 8, 96]} />
        <meshBasicMaterial color="#6b8bff" transparent opacity={0.28} />
      </mesh>

      {positions.map((p, i) => {
        const c = palette[i % 3];
        return (
          <mesh
            key={i}
            ref={(n) => {
              nodeRefs.current[i] = n;
            }}
            position={[p[0], p[1], p[2]]}
            scale={0.8}
          >
            <sphereGeometry args={[0.17, 12, 12]} />
            <meshStandardMaterial
              color={c}
              emissive={c}
              emissiveIntensity={1.4}
              transparent
              opacity={0.9}
            />
          </mesh>
        );
      })}
    </group>
  );
}