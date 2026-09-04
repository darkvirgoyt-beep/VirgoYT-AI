#!/usr/bin/env bash
# Live Cloud Computer launcher — browser-accessible XFCE desktop for proot/Termux.
# Starts Xvfb + XFCE + x11vnc + websockify(noVNC) so the desktop is usable from any browser.
set -Eeuo pipefail

: "${DISPLAY:=:1}"
: "${VNC_PORT:=5901}"
: "${WEB_PORT:=6080}"
: "${RESOLUTION:=1440x900x24}"

# Fix broken X11 socket dir (a common cause of 'ruining' under termux-x11)
if [[ -L /tmp/.X11-unix ]]; then rm -f /tmp/.X11-unix; fi
mkdir -p /tmp/.X11-unix && chmod 1777 /tmp/.X11-unix

cleanup() { kill 0 2>/dev/null || true; }
trap cleanup EXIT INT TERM

echo "Starting Xvfb on $DISPLAY ..."
Xvfb "$DISPLAY" -screen 0 "$RESOLUTION" -ac +extension GLX +render -noreset >/tmp/xvfb.log 2>&1 &

# Wait until the X socket exists before launching a session tied to it
for i in $(seq 1 20); do
  [[ -e "/tmp/.X11-unix/X${DISPLAY#*:}" ]] && break
  sleep 0.5
done
sleep 1

echo "Starting XFCE on existing display $DISPLAY ..."
export DISPLAY
dbus-launch --sh-syntax >/tmp/dbus.env 2>/dev/null || true
source /tmp/dbus.env 2>/dev/null || true
startxfce4 >/tmp/xfce.log 2>&1 &
sleep 5

echo "Starting x11vnc on :$VNC_PORT ..."
x11vnc -display "$DISPLAY" -forever -shared -noxdamage -noshm -rfbport "$VNC_PORT" >/tmp/x11vnc.log 2>&1 &
sleep 2

echo "Starting websockify (noVNC) on :$WEB_PORT -> localhost:$VNC_PORT ..."
exec websockify --web=/usr/share/novnc "$WEB_PORT" "127.0.0.1:$VNC_PORT"