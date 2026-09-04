#!/usr/bin/env bash
set -Eeuo pipefail

: "${DISPLAY:=:1}"
: "${VNC_PORT:=5901}"
: "${PORT:=${NOVNC_PORT:-8080}}"
: "${DESKTOP_PASSWORD:=change-me-before-deploy}"
: "${RESOLUTION:=1440x900x24}"

if [[ "${DESKTOP_PASSWORD}" == "change-me-before-deploy" ]]; then
  echo "DESKTOP_PASSWORD must be set to a strong secret before exposing VirgoYT Computer" >&2
  exit 1
fi

# Fix broken X11 socket dir (a common cause of desktop failures in proot/containers)
if [[ -L /tmp/.X11-unix ]]; then rm -f /tmp/.X11-unix; fi
mkdir -p /tmp/.X11-unix && chmod 1777 /tmp/.X11-unix

# Make the (possibly volume-backed) home directory writable by the desktop user
chown -R virgo:virgo /home/virgo 2>/dev/null || true

mkdir -p /home/virgo/.vnc
x11vnc -storepasswd "${DESKTOP_PASSWORD}" /home/virgo/.vnc/passwd >/dev/null
chown -R virgo:virgo /home/virgo/.vnc

cleanup() {
  kill 0 2>/dev/null || true
}
trap cleanup EXIT INT TERM

Xvfb "${DISPLAY}" -screen 0 "${RESOLUTION}" -ac +extension GLX +render -noreset >/tmp/xvfb.log 2>&1 &

# Wait until the X socket exists before launching a session tied to it
for i in $(seq 1 20); do
  [[ -e "/tmp/.X11-unix/X${DISPLAY#*:}" ]] && break
  sleep 0.5
done
sleep 1

export DISPLAY
sudo -u virgo env HOME=/home/virgo DISPLAY="${DISPLAY}" dbus-run-session -- startxfce4 >/tmp/xfce.log 2>&1 &
sleep 4

x11vnc -display "${DISPLAY}" \
  -rfbauth /home/virgo/.vnc/passwd \
  -rfbport "${VNC_PORT}" \
  -forever -shared -noxdamage -noshm -ncache 10 -listen 0.0.0.0 \
  >/tmp/x11vnc.log 2>&1 &

# Railway routes HTTP traffic to $PORT. websockify serves noVNC and proxies
# its WebSocket connection to the password-protected VNC service.
exec websockify --web=/usr/share/novnc "0.0.0.0:${PORT}" "127.0.0.1:${VNC_PORT}"