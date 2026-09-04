FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive \
    DISPLAY=:1 \
    HOME=/home/virgo \
    NOVNC_PORT=8080 \
    VNC_PORT=5901

# Base + desktop environment + apps (no snap transitional packages — use real browsers)
RUN apt-get update && apt-get install -y --no-install-recommends \
    xfce4 xfce4-terminal dbus-x11 \
    xvfb x11vnc novnc websockify \
    thunar mousepad ristretto \
    libreoffice-writer libreoffice-calc libreoffice-impress \
    pulseaudio pavucontrol \
    curl ca-certificates wget gnupg sudo supervisor \
    fonts-dejavu fonts-liberation fonts-noto-color-emoji fonts-noto-cjk \
    htop neofetch \
    && rm -rf /var/lib/apt/lists/*

# Google Chrome (real browser, works in Docker)
RUN wget -q -O /tmp/chrome.deb \
    "https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb" \
    && apt-get install -y --no-install-recommends /tmp/chrome.deb \
    && rm -f /tmp/chrome.deb

# Firefox ESR via Mozilla's official apt repo
RUN install -d -m 0755 /etc/apt/keyrings \
    && wget -qO- https://packages.mozilla.org/apt/repo-signing-key.gpg \
       | tee /etc/apt/keyrings/packages.mozilla.org.asc >/dev/null \
    && echo 'deb [signed-by=/etc/apt/keyrings/packages.mozilla.org.asc] https://packages.mozilla.org/apt mozilla main' > /etc/apt/sources.list.d/mozilla.list \
    && apt-get update && apt-get install -y --no-install-recommends firefox \
    && rm -rf /var/lib/apt/lists/*

RUN useradd --create-home --shell /bin/bash virgo \
    && echo 'virgo ALL=(ALL) NOPASSWD:ALL' >/etc/sudoers.d/virgo \
    && chmod 0440 /etc/sudoers.d/virgo \
    && mkdir -p /home/virgo/.vnc /var/log/supervisor /tmp/.X11-unix \
    && chown -R virgo:virgo /home/virgo \
    && chmod 1777 /tmp/.X11-unix

COPY desktop/start-desktop.sh /usr/local/bin/start-desktop.sh
RUN chmod +x /usr/local/bin/start-desktop.sh

WORKDIR /home/virgo
VOLUME ["/home/virgo"]
EXPOSE 8080

CMD ["/usr/local/bin/start-desktop.sh"]