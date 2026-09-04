'use client';

import { useState, useEffect } from 'react';

// Reusable mobile/tablet detection for responsive workspace layout.
export function useIsMobile(breakpoint = 768) {
  const [mobile, setMobile] = useState(false);

  useEffect(() => {
    const check = () => setMobile(window.innerWidth < breakpoint);
    let t: ReturnType<typeof setTimeout> | undefined;
    const onResize = () => {
      clearTimeout(t);
      t = setTimeout(check, 100);
    };
    check();
    window.addEventListener('resize', onResize);
    return () => {
      clearTimeout(t);
      window.removeEventListener('resize', onResize);
    };
  }, [breakpoint]);

  return mobile;
}

// Tracks the visual viewport height (shrinks when mobile keyboard opens)
// and exposes it so components can adjust their layout.
export function useVisualViewportHeight() {
  const [vh, setVh] = useState<string>('100dvh');

  useEffect(() => {
    const apply = () => {
      const vvh = window.visualViewport?.height;
      if (vvh) setVh(vvh + 'px');
      else setVh(window.innerHeight + 'px');
    };
    apply();
    window.visualViewport?.addEventListener('resize', apply);
    window.visualViewport?.addEventListener('scroll', apply);
    return () => {
      window.visualViewport?.removeEventListener('resize', apply);
      window.visualViewport?.removeEventListener('scroll', apply);
    };
  }, []);

  return vh;
}