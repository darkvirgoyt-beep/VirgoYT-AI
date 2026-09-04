#!/usr/bin/env bash
set -Eeuo pipefail

: "${DISPLAY:=:1}"
: "${VNC_PORT:=5901}"
: "${PORT:=${NOVNC_PORT:-8080}}"
: "${DESKTOP_PASSWORD:=change-me-before-deploy}"

if [[ "${DESKTOP_PASSWORD}" == "change-me-before-deploy" ]]; then
  echo "DESKTOP_PASSWORD must be set to a strong secret before exposing VirgoYT Computer" >&2
  exit 1
fi

mkdir -p /home/virgo/.vnc
x11vnc -storepasswd "${DESKTOP_PASSWORD}" /home/virgo/.vnc/passwd >/dev/null
chown -R virgo:virgo /home/virgo/.vnc

cleanup() {
  kill 0 2>/dev/null || true
}
trap cleanup EXIT INT TERM

Xvfb "${DISPLAY}" -screen 0 1440x900x24 -ac +extension GLX +render -noreset >/tmp/xvfb.log 2>&1 &
sleep 2

sudo -u virgo env HOME=/home/virgo DISPLAY="${DISPLAY}" dbus-run-session -- startxfce4 >/tmp/xfce.log 2>&1 &
sleep 3

x11vnc -display "${DISPLAY}" \
  -rfbauth /home/virgo/.vnc/passwd \
  -rfbport "${VNC_PORT}" \
  -forever -shared -noxdamage -ncache 10 -listen 0.0.0.0 \
  >/tmp/x11vnc.log 2>&1 &

# Railway routes HTTP traffic to $PORT. websockify serves noVNC and proxies
# its WebSocket connection to the password-protected VNC service.
exec websockify --web=/usr/share/novnc "0.0.0.0:${PORT}" "127.0.0.1:${VNC_PORT}"
