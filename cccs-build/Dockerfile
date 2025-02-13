ARG OPENVSX_IMAGES_TAG
FROM uchimera.azurecr.io/cccs/openvsx/webui:${OPENVSX_IMAGES_TAG} AS webui
FROM uchimera.azurecr.io/cccs/openvsx/server:${OPENVSX_IMAGES_TAG} AS server

# Copy the UI assets for Spring to server
COPY --from=webui --chown=openvsx:openvsx /home/node/webui/static/ BOOT-INF/classes/static/
