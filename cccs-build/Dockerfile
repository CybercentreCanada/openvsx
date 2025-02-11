ARG OPENVSX_IMAGES_TAG
FROM uchimera.azurecr.io/cccs/openvsx/webui:${OPENVSX_IMAGES_TAG} AS webui
FROM uchimera.azurecr.io/cccs/openvsx/server:${OPENVSX_IMAGES_TAG} AS server

FROM ubuntu AS builder

RUN apt-get update \
    && apt-get install --no-install-recommends -y \
        bash \
        ca-certificates \
        git \
        curl \
        supervisor \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create Supervisor config directory
RUN mkdir -p /etc/supervisor/conf.d/

RUN curl -sSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs

# Copy the UI assets
COPY --from=webui /home/node/webui/static/ /home/node/webui/static/
# Copy the Express server
COPY --from=webui /home/node/webui/lib/default/ /home/node/webui/lib/default/

# Copy the Spring Boot server files
COPY --from=server /home/openvsx/ /home/openvsx/
COPY --from=server /home/openvsx/server/run-server.sh /home/openvsx/server/run-server.sh
RUN chmod +x /home/openvsx/server/run-server.sh

# Set working directory for UI
WORKDIR /home/node/webui

# Ensure dependencies exist for Express
RUN yarn add express@4.21.0
RUN yarn add express-rate-limit@7.4.0

# Create Supervisor config file
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Expose ports for both services
EXPOSE 8080 3000

# Set entrypoint to Supervisor
CMD ["supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
