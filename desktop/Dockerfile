FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive \
    DISPLAY=:1 \
    HOME=/home/virgo \
    NOVNC_PORT=8080 \
    VNC_PORT=5901

RUN apt-get update && apt-get install -y --no-install-recommends \
    xfce4 xfce4-terminal dbus-x11 \
    xvfb x11vnc novnc websockify \
    chromium curl ca-certificates sudo supervisor \
    fonts-dejavu fonts-liberation fonts-noto-color-emoji \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --create-home --shell /bin/bash virgo \
    && echo 'virgo ALL=(ALL) NOPASSWD:ALL' >/etc/sudoers.d/virgo \
    && chmod 0440 /etc/sudoers.d/virgo \
    && mkdir -p /home/virgo/.vnc /var/log/supervisor \
    && chown -R virgo:virgo /home/virgo

COPY desktop/start-desktop.sh /usr/local/bin/start-desktop.sh
RUN chmod +x /usr/local/bin/start-desktop.sh

WORKDIR /home/virgo
VOLUME ["/home/virgo"]
EXPOSE 8080

CMD ["/usr/local/bin/start-desktop.sh"]
