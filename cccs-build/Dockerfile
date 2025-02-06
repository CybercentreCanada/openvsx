FROM ubuntu AS builder
FROM cccs/openvsx/webui:latest AS webui
FROM cccs/openvsx/server:latest AS server

COPY --from=webui /home/node/webui/static/ /BOOT-INF/classes/static/
