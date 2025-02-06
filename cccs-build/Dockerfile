ARG OPENVSX_IMAGES_TAG
FROM ubuntu AS builder
FROM cccs/openvsx/webui:${OPENVSX_IMAGES_TAG} AS webui
FROM cccs/openvsx/server:${OPENVSX_IMAGES_TAG} AS server

COPY --from=webui /home/node/webui/static/ /BOOT-INF/classes/static/
