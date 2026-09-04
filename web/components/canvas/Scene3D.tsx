'use client';

import { useState } from 'react';
import { Canvas, useThree } from '@react-three/fiber';
import { Float, Sparkles, Stars } from '@react-three/drei';
import { Particles } from './Particles';
import { HolographicGrid } from './HolographicGrid';
import { HolographicCore } from './HolographicCore';
import { AgentNodeNetwork } from './AgentNodeNetwork';
import { OrbitRings } from './OrbitRings';
import { OrbitControls } from '@react-three/drei';
import { ConnectionStatus } from './ConnectionStatus';

function AtmosphericFog() {
  useThree(({ gl }) => {
    if ('setClearColor' in gl) {
      gl.setClearColor('#05060f', 0);
    }
  });
  return null;
}

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
        <AtmosphericFog />

        <HolographicGrid />
        <Stars radius={90} depth={60} count={4000} factor={5} saturation={0} fade speed={0.6} />
        <Particles count={4500} />
        <OrbitRings />
        <AgentNodeNetwork />
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
