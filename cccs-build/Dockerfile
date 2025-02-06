# Builder image to compile the website
FROM ubuntu AS builder

WORKDIR /workdir

RUN apt-get update \
  && apt-get install --no-install-recommends -y \
  bash \
  ca-certificates \
  git \
  curl \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*

# See https://github.com/nodesource/distributions/blob/main/README.md#debinstall
RUN curl -sSL https://deb.nodesource.com/setup_20.x | bash - \
  && apt-get install -y nodejs

RUN npm install --global yarn@4.*

COPY . /workdir
RUN /usr/bin/yarn --cwd webui \
  && /usr/bin/yarn --cwd webui build \
  && /usr/bin/yarn --cwd webui build:default

# Use the locally built server image instead of pulling from a registry
FROM cccs/openvsx/server:latest

COPY --from=builder --chown=openvsx:openvsx /workdir/webui/static/ BOOT-INF/classes/static/
