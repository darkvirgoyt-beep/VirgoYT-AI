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