'use client';

import { useState } from 'react';
import { Canvas, useThree } from '@react-three/fiber';
import { Float, Sparkles, Stars } from '@react-three/drei';
import { Particles } from './Particles';
import { HolographicGrid } from './HolographicGrid';
import { HolographicCore } from './HolographicCore';
import { OrbitControls } from '@react-three/drei';
import { ConnectionStatus } from './ConnectionStatus';

export function Scene3D({
  onReady,
  disconnected,
}: {
  onReady?: () => void;
  disconnected?: boolean;
}) {
  const [loaded, setLoaded] = useState(false);

  return (
    <div className="absolute inset-0 w-full h-full">
      <Canvas
        dpr={[1, 2]}
        camera={{ position: [0, 2, 12], fov: 60 }}
        gl={{ antialias: true, alpha: true, powerPreference: 'high-performance' }}
        onCreated={() => {
          setLoaded(true);
          onReady?.();
        }}
      >
        <ambientLight intensity={0.3} />
        <directionalLight position={[5, 10, 5]} intensity={0.8} color="#3375ff" />
        <directionalLight position={[-5, -5, -5]} intensity={0.4} color="#b967ff" />

        <HolographicGrid />
        <Stars radius={80} depth={50} count={3000} factor={4} saturation={0} fade speed={0.5} />
        <Particles count={4000} />
        <Float speed={1.5} rotationIntensity={0.3} floatIntensity={1}>
          <HolographicCore />
        </Float>

        <ConnectionStatus active={!disconnected} />

        <OrbitControls
          enablePan={false}
          enableZoom={false}
          autoRotate
          autoRotateSpeed={0.4}
          maxPolarAngle={Math.PI / 2.2}
          minPolarAngle={Math.PI / 3}
        />
      </Canvas>
    </div>
  );
}
